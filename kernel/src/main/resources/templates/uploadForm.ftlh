<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Upload chat log</title>
</head>

<body>
<form name="uploadingForm" enctype="multipart/form-data" action="/upload" method="POST">
    <p>
        <input id="file" type="file" name="file">
    </p>
    <p>
        <input type="submit" value="Загрузить лог чата">
    </p>
</form>
</body>
</html>