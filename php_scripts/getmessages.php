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
if (isset($_GET["username"])) { 
    $username = $_GET['username'];
	if(isset($_GET["time"])){
		$time = $_GET['time'];
		$q = mysql_query("SELECT * FROM Conversations where username = '$username'");
		while($row = mysql_fetch_assoc($q)){
			$conversationID = $row['conversationID'];
			$q1 = mysql_query("SELECT * FROM MessagesMain where conversationID = '$conversationID' && time > '$time'");
			while($row1 = mysql_fetch_assoc($q1)){
				$output[] = $row1;
			}
			
		}
		print(json_encode($output));
		
		
	}
}


?>