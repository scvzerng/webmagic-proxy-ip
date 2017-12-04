<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet"  type="text/css" href="/semantic/semantic.min.css"/>
    <script src="/jquery/jquery-3.2.1.min.js"></script>
    <script src="/semantic/semantic.min.js"></script>
</head>
<body class="ui fluid container">
<div class="ui one item menu">
    <a class="active item">代理IP</a>

</div>
<div class="ui primary button" onclick="$.get('/ips/fetch')">爬取IP</div>
<div class="ui green button" id="addIp">

</div>
<script>
    $("#addIp").click(function () {

    })
</script>
</html>