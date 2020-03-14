<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="/image/favicon.ico">

    <title>Dashboard Template for Bootstrap</title>

    <!-- Bootstrap core CSS -->
    <link href="boostrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="css/dashboard.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>

<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                    data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">CaoyxRpc-Admin</a>
        </div>
        <#--        <div id="navbar" class="navbar-collapse collapse">-->
        <#--            <ul class="nav navbar-nav navbar-right">-->
        <#--                <li><a href="#">Dashboard</a></li>-->
        <#--                <li><a href="#">Settings</a></li>-->
        <#--                <li><a href="#">Profile</a></li>-->
        <#--                <li><a href="#">Help</a></li>-->
        <#--            </ul>-->
        <#--            <form class="navbar-form navbar-right">-->
        <#--                <input type="text" class="form-control" placeholder="Search...">-->
        <#--            </form>-->
        <#--        </div>-->
    </div>
</nav>

<div class="container-fluid">
    <div class="row">
        <div class="col-sm-3 col-md-2 sidebar">
            <ul class="nav nav-sidebar">
                <li class="active"><a href="#">总览<span class="sr-only">(current)</span></a></li>
                <li><a href="#">服务</a></li>
                <li><a href="#">分析</a></li>
            </ul>
        </div>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <h1 class="page-header">Dashboard</h1>

            <#--            <div class="row placeholders">-->
            <#--                <div class="col-xs-6 col-sm-3 placeholder">-->
            <#--                    <img src="data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" width="200" height="200" class="img-responsive" alt="Generic placeholder thumbnail">-->
            <#--                    <h4>Label</h4>-->
            <#--                    <span class="text-muted">Something else</span>-->
            <#--                </div>-->
            <#--                <div class="col-xs-6 col-sm-3 placeholder">-->
            <#--                    <img src="data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" width="200" height="200" class="img-responsive" alt="Generic placeholder thumbnail">-->
            <#--                    <h4>Label</h4>-->
            <#--                    <span class="text-muted">Something else</span>-->
            <#--                </div>-->
            <#--                <div class="col-xs-6 col-sm-3 placeholder">-->
            <#--                    <img src="data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" width="200" height="200" class="img-responsive" alt="Generic placeholder thumbnail">-->
            <#--                    <h4>Label</h4>-->
            <#--                    <span class="text-muted">Something else</span>-->
            <#--                </div>-->
            <#--                <div class="col-xs-6 col-sm-3 placeholder">-->
            <#--                    <img src="data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" width="200" height="200" class="img-responsive" alt="Generic placeholder thumbnail">-->
            <#--                    <h4>Label</h4>-->
            <#--                    <span class="text-muted">Something else</span>-->
            <#--                </div>-->
            <#--            </div>-->

            <h2 class="sub-header">服务列表</h2>
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>providerName</th>
                        <th>className</th>
                        <th>ImplVersion</th>
                        <th>HostPort</th>
                        <th>Metadata</th>
                    </tr>
                    </thead>
                    <tbody>

                    <#list providerInfos as info>
                        <tr>
                        <th>${providerName}</th>
                        <th>${classInfo.className}</th>
                        <th>${classInfo.version}</th>
                        <th>${info.ipPort}</th>
                        <th>${info.metadata!"null"}</th>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
<script>window.jQuery || document.write('<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"><\/script>')</script>
<script src="/boostrap/js/bootstrap.min.js"></script>
</body>
</html>
