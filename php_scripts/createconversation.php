<?php
$mysql_host = "localhost"; // sql сервер
$mysql_user = "kaslad"; // пользователь
$mysql_password = "CdRoom301"; // пароль
$mysql_database = "e-chat"; // имя базы данных chat

$link = mysql_connect($mysql_host, $mysql_user, $mysql_password); // коннект к серверу SQL
if ($link == false) {
    print("sql doesnot connected");
}
$link = mysql_select_db($mysql_database); // коннект к БД на сервере
if ($link == false) {
    print("e-chat doesnot connected");
}
mysql_set_charset('utf8'); // кодировка
if(isset($_GET["user1"])){
    $user1 = $_GET['user1'];
    if(isset($_GET["user2"])){
        $user2 = $_GET['user2'];
        $finduser = mysql_query("SELECT * FROM Users where username = '$user1' ");
        if(mysql_num_rows($finduser) > 0){
            $finduser = mysql_query("SELECT * FROM Users where username = '$user2' ");
            if(mysql_num_rows($finduser) > 0){
                // last added row and its convID
                $q = mysql_query("SELECT * FROM Conversations WHERE id = (SELECT MAX(id) FROM Conversations)");
                $last = mysql_fetch_assoc($q);
                $conversationID = $last['conversationID'] + 1;
                $time = round(microtime(1) * 1000);

                mysql_query("INSERT INTO `Conversations` (`conversationID`, `username`, `time` ) VALUES ('$conversationID', '$user1','$time' )");
                mysql_query("INSERT INTO `Conversations` (`conversationID`, `username`, `time` ) VALUES ('$conversationID', '$user2','$time' )");
                print("success");
                
            } else{
                print("fail");
            }
        } else {
            print("fail");
        }
        
    }
}
?>