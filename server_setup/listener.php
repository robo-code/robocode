<?php
define( "TYROOT", dirname(__FILE__).'/' );
$logs = date('D M j');
$myFile = TYROOT."../virtual-combat/logs/$logs.txt";
$fh = fopen($myFile, 'a');
$date = date('c');
$stringData = "TIME : $date PATH1 : ".$_POST['path1']." Path2 : ".$_POST['path2']." Token : ".$_POST['key']."\n";
fwrite($fh, $stringData);
//fclose($fh);

$conn = @mysql_connect('localhost', 'root', '!(en$p1c3');
		if($conn==false ) {
			$err = mysql_errno();
			if( $err==1203 ) {
				fwrite($fh,'Our database server is overcome with connections. Please try after some time.');
			}
			fwrite($fh,'DB::constructor -- could not connect to db host!');
		}
		@mysql_select_db('robodata', $conn)
			or fwrite($fh,'DB::constructor -- could not select db!');

$sql="INSERT INTO BATTLEDATA (team1, team2, seckey, score1, score2, status, time, attempts) VALUES ('".$_POST['path1']."','".$_POST['path2']."','".$_POST['key']."',0,0,0,1,0)";

//fclose($fh);
if (!mysql_query($sql,$conn))
  {
  echo 2;
  fwrite($fh,"insert nahi ho raha");
  }
fclose($fh);
echo 1;
mysql_close($conn);

//checkPid("java");
/*
function checkPid($name)

{

     // create our system command

     $cmd = "ps -C $name";

 

     // run the system command and assign output to a variable ($output)

  //   exec($cmd, $output, $result);
//echo count($output);
if(count($output)<3){

	exec("nohup nice -n 10 /usr/lib/jvm/jre/bin/java -classpath './:/var/www/html/tankyudh_cfitbhu/virtual-combat/libs/*:/var/www/html/tankyudh_cfitbhu/virtual-combat/*' BattleRunner >/dev/null 2> /dev/null & ");
	echo "started new instance of master\r";
}
else{
	echo "no need to start java";
}


 


}
*/
?>
