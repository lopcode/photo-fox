<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="/static/css/style.css">
    <link rel="preload" href="/static/css/normalize.css" as="style">
    <title>upload a photo • photo fox</title>
</head>
<body>
<form id="upload" method="post" enctype="multipart/form-data" action="/api/v1/upload_photo">
    <div>
        <label for="photo">choose a photo to upload...</label>
        <input type="file" accept="image/*" id="photo" name="photo" />
    </div>
    <div>
        <button type="submit" form="upload">submit</button>
    </div>
</form>
</body>
</html>