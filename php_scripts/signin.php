<?php
$mysql_host = "localhost"; // sql сервер
$mysql_user = "kaslad"; // пользователь
$mysql_password = "CdRoom301"; // пароль
$mysql_database = "e-chat"; // имя базы данных chat
//chat.php?action=get&time=303030030



$link = mysql_connect($mysql_host, $mysql_user, $mysql_password); // коннект к серверу SQL
if ($link == false) {
    print("sql doesnot connected");
}
$link = mysql_select_db($mysql_database); // коннект к БД на сервере
if ($link == false) {
    print("e-chat doesnot connected");
}
mysql_set_charset('utf8'); // кодировка
if (isset($_GET["username"])) { 
    $username = $_GET['username'];
    if (isset($_GET["password"])){
        $password = $_GET['password'];
        $q=mysql_query("SELECT * FROM Users WHERE password = '$password' && username = '$username'");
        if( mysql_num_rows($q) == 1){
            print("success");
        }
        if( mysql_num_rows($q) == 0){
            print("fail");
        }
        
    
    }
    else{
        print("fail");
    }
    }else{
        print("fail");
    }
    
    




mysql_close();

?>