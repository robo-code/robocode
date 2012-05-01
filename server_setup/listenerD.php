<?php
define( "TYROOT", dirname(__FILE__).'/' );

$folder = "../ladoaurmaro/robots/";
$savepath = TYROOT.$folder;
$codesavename = $_POST['path1'];
$code = $_FILES['jarfile'];

$logs = date('D M j');
$myFile = TYROOT."../ladoaurmaro/logs/$logs.txt";
$fh = fopen($myFile, 'a');
$date = date('c');
$stringData = "Time : $date PATH1 : ".$_POST['path1']." Path2 : ".$_POST['path2']." Token : ".$_POST['key']." Tmp : ".$code['tmp_name']."\n";
fwrite($fh, $stringData);
fclose($fh);

$result = "Success.";

if( !move_uploaded_file($code['tmp_name'], $savepath.$codesavename.".jar") ){
$result = "Error moving uploaded file. @$savepath.$codesavename";
echo 2;
}

$conn = @mysql_connect('localhost:3306', 'root', '!(en$p1c3');
                if($conn==false ) {
                        $err = mysql_errno();
                        if( $err==1203 ) {
                                fwrite($fh,'Our database server is overcome with connections. Please try after some time.');
                        }
                        fwrite($fh,'DB::constructor -- could not connect to db host!');
			echo 2;
                }
                @mysql_select_db('robodata', $conn)
                       or fwrite($fh,'DB::constructor -- could not select db!');

$sql="INSERT INTO BATTLEDATA (team1, team2, seckey, score1, score2, status, time, attempts) VALUES ('".$_POST['path1']."','".$_POST['path2']."','".$_POST['key']."',0,0,0,1,0)";



if (!mysql_query($sql,$conn))
  {
  $result = "Error in insering into database.";
echo 2;
  }

mysql_close($conn);

if($result == "Success.")
	echo 1;

$fh = fopen($myFile, 'a');
$stringData = "Result $result\n";
fwrite($fh, $stringData);
fclose($fh);

/*
checkPid("master");
function checkPid($name)


{

     // create our system command

     $cmd = "ps -C $name";

 

     // run the system command and assign output to a variable ($output)

     exec($cmd, $output, $result);
echo "hello \n";
echo count($output);
if(count($output)<3){
	echo "starting . . . . \r";
	exec("nohup nice -n 10 /var/www/html/tankyudh_cfitbhu/virtual-combat/master >/dev/null 2> /dev/null & ");
	echo "started new \r";
}
else{
echo "done \r";
}


 


}
*/
?> 
