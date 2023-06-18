import pika, sys, os
import tensorflow as tf
import pandas as pd
import numpy as np
import json

from pika import BasicProperties
from transformers import AutoTokenizer
from collections import defaultdict


def preprocess_question_data_function(text):
    return bert_tokenizer(text, truncation=True, max_length=64)


def preprocess_qa_data_function(pair):
    return bert_tokenizer(pair['q'], pair['a'], truncation=True, max_length=128, padding="max_length")


def predict_questions(question_texts):
    print("Question tokenization start")
    question_texts_tokenized = list(map(preprocess_question_data_function, question_texts))
    print("Question tokenization completed")
    question_predictions = []
    counter = 1
    for text in question_texts_tokenized:
        if counter % 10 == 0:
            print(f"Processing question {counter}/{len(question_texts_tokenized)}")
        tokens = defaultdict(list)
        tokens['input_ids'].append(text['input_ids'])
        tokens['token_type_ids'].append(text['token_type_ids'])
        tokens['attention_mask'].append(text['attention_mask'])
        logits = bert_q(tokens)
        predictions = np.argmax(logits['logits'], axis=1)
        question_predictions.append(predictions[0])
        counter += 1
    return question_predictions


def predict_answers(questions_answers_to_predict):
    results = []
    counter = 1
    for row in questions_answers_to_predict:
        if counter % 10 == 0:
            print(f"Processing q/a {counter}/{len(questions_answers_to_predict)}")
        pairs = []
        for answer in row['answers']:
            pairs.append({
                'q': row['question_text'],
                'a': answer['text']
            })

        pairs_tokenized = list(map(preprocess_qa_data_function, pairs))
        pairs_predictions = []
        for text in pairs_tokenized:
            tokens = defaultdict(list)
            tokens['input_ids'].append(text['input_ids'])
            tokens['token_type_ids'].append(text['token_type_ids'])
            tokens['attention_mask'].append(text['attention_mask'])
            logits = bert_qa(tokens)
            predictions = np.argmax(logits['logits'], axis=1)
            pairs_predictions.append(predictions[0])

        ans_res = []
        for idx, pred in enumerate(pairs_predictions):
            ans_res.append({
                'idx': idx,
                'label': pred
            })
        results.append({
            'q_idx': row['question_idx'],
            'a_res': ans_res
        })
        counter += 1

    return results


def main(bert_tokenizer, bert_q, bert_qa):
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()

    channel.queue_declare(queue='mark_discussions')
    channel.queue_declare(queue='labeled_discussions')

    def callback(ch, method, properties, body):
        print("Message accepted")
        message_json = json.loads(body.decode("utf-8"))
        print(message_json)
        question_texts = []
        for idx, row in enumerate(message_json):
            question_text = row['question']['text']
            question_texts.append(question_text)

        print("Question predict start")
        question_predictions = predict_questions(question_texts)
        print("Question predict completed")

        questions_answers_to_predict = []

        for idx, row in enumerate(question_predictions):
            message_json[idx]['question']['label'] = int(row)
            if row == 0:
                continue
            answers = []
            for _idx, _row in enumerate(message_json[idx]['answers']):
                answers.append({
                    "text": _row['text'],
                    "inx_in_json": _idx
                })
            if len(answers) == 0:
                continue
            questions_answers_to_predict.append({
                "question_text": question_texts[idx],
                "question_idx": idx,
                "answers": answers
            })

        print("Answer predict start")
        answer_predictions = predict_answers(questions_answers_to_predict)
        print("Answer predict completed")

        for row in answer_predictions:
            idx = row['q_idx']
            for _row in row['a_res']:
                message_json[idx]['answers'][int(_row['idx'])]['label'] = int(_row['label'])

        properties = BasicProperties(
            app_id='classifiers',
            content_type='application/json',
            content_encoding='utf-8',
            delivery_mode=2,
        )
        channel.basic_publish(exchange='', routing_key='labeled_discussions',
                              body=json.dumps(message_json, ensure_ascii=False).encode("utf-8"), properties=properties)
        print("Message processed")

    channel.basic_consume(queue='mark_discussions', on_message_callback=callback, auto_ack=True)

    print(' [*] Waiting for messages. To exit press CTRL+C')
    channel.start_consuming()


if __name__ == '__main__':
    bert_tokenizer = AutoTokenizer.from_pretrained("ai-forever/sbert_large_mt_nlu_ru")

    print("Начало загрузки классификатора вопросов")
    bert_q = tf.saved_model.load('../models/bert_q')
    print("Загрузка классификатора вопросов завершена")
    print("Начало загрузки классификатора пар вопрос/ответ")
    bert_qa = tf.saved_model.load('../models/bert_qa')
    print("Загрузка классификатора пар вопрос/ответ завершена")
    try:
        main(bert_tokenizer, bert_q, bert_qa)
    except KeyboardInterrupt:
        print('Interrupted')
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)
