/**
TO COMPILE:
g++ -o master $(mysql_config --cflags) -L/usr/lib64/mysql -lmysqlclient MasterProgram.cpp
TO RUN:
./master
*/

#include <iostream>
#include <stdio.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/param.h>
#include <sys/times.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <signal.h>
#include <time.h>
#include <mysql.h>

using namespace std;
#define NO_INSTANCES 5
#define TIME_SPAN_MINUTES 3

char program[400] = "java  -Xmx768m -classpath './:/var/www/html/tankyudh_cfitbhu/virtual-combat/libs/*:/var/www/html/tankyudh_cfitbhu/virtual-combat/*' BattleRunner";

int  main(int argc,char* argv[]) 
{
  pid_t child;
  int   cstatus;  /* Exit status of child. */
  pid_t c;          /* Pid of child to be returned by wait. */
  int i,k;
  int val=100;
  char arr[7];
  time_t start;
  time_t end;
  
  long t1,t2;
  
  char prog[450];


	
   /************************
	connect to database
    ************************/
   MYSQL *conn;
   MYSQL_RES *res;
   MYSQL_ROW row;


  MYSQL_RES *result;


  int num_fields;

   char *server = "localhost";
   char *user = "root";
   char *password = "3pq,x-w6"; /* set me first */
   char *database = "robodata";

   conn = mysql_init(NULL);

   /* Connect to database */
   if (!mysql_real_connect(conn, server,user, password, database, 0, NULL, 0)) {
      fprintf(stderr, "%s\n", mysql_error(conn));
      exit(1);
   }
   
   if (mysql_query(conn, "use robodata;")) {
			      fprintf(stderr, "%s\n", mysql_error(conn));
			      exit(1);
			   }
    // connected

  
   for(int i=0;i<30;i++)
   {
   	   cout<<"Starting instance no## "<<i<<endl;
	   start=time(NULL);
//	   t1=times(&mstart);
	   child=fork();
		if (child == 0) {
		  char query[200];
		  /* Child process. To begin with, it prints its pid. */
		  printf("Child: PID of Child = %ld\n", (long) getpid());
		  sprintf(prog,"%s > ./BattleRunnerLogs/LOGinstance_no%d_parent%ld.txt",program,i,(long)getppid());
		  cout<<"Opening a program ### "<<prog<<endl;
  		  system(prog);
		   _exit(1);
		}
		else
		 { 
		 /* Parent process. */
		  	if (child == (pid_t)(-1)) 
		  	{
		    	 fprintf(stderr, "Fork failed.\n"); exit(1);
		  	}
		  	else 
		  	{
		     //c = wait(&cstatus); /* Wait for child to complete. */


		     	  cout<<"here1"<<endl;

            		  MYSQL_ROW row2;
			  MYSQL_RES *result3=NULL;
			  MYSQL_RES *result2=NULL;

			  if(mysql_query(conn, "SELECT UNIX_TIMESTAMP(CURRENT_TIMESTAMP)"))
			  {
			      fprintf(stderr, "%s\n", mysql_error(conn));
			      continue;
			   }
             		     	cout<<"here2"<<endl;
			  result2 = mysql_store_result(conn);
			 
			  num_fields = mysql_num_fields(result2);

			  row2 = mysql_fetch_row(result2);
			  cout<<"here2  "<<result2<<"  "<<num_fields<<"   "<<row2[0]<<endl;
			  long time2 = (long)row2[0];
			  mysql_free_result(result2);
			  char query2[200];
			  		     	cout<<"here3"<<endl;
			  sprintf(query2,"SELECT pid FROM processes WHERE status = 0 and start_time < %ld",time2-(60*TIME_SPAN_MINUTES*2));
			  printf("Query = %s\n",query2);
			  if(mysql_query(conn, query2))
			  {
			      fprintf(stderr, "%s\n", mysql_error(conn));
			      exit(1);
			   }
			   		     	cout<<"here4"<<endl;
			  result3 = mysql_store_result(conn);
			  if(result3!=NULL)
			  {
				cout<<"here2  "<<result3<<endl;
				  num_fields = mysql_num_fields(result3);

				cout<<"here2  "<<result3<<"  "<<num_fields<<"   "<<row2[0]<<endl;
				  
				  char killCommand[200];

				  
				  while ((row2 = mysql_fetch_row(result3)))
				  {
					  cout<<"killing..."<<endl;
				          printf("killing java program with pid %s ", (row2[0] ? row2[0] : "NULL"));
				          if(row2[0] != NULL)
				          {
						  sprintf(killCommand,"kill -9 %s",row2[0]);
						  cout<<"killcommand: "<<killCommand<<endl;
						  system(killCommand);
						  sprintf(query2,"UPDATE processes SET status=2 WHERE pid=%s;",row2[0]);
						  cout<<"Executing query"<<query2<<endl;
						  if (mysql_query(conn, query2)) {
					      		fprintf(stderr, "%s\n", mysql_error(conn));
					      		exit(1);
					   	  }
					  }
				  //result = mysql_store_result(conn);
		  
				  }
			   }	  

			  
		     sleep(60*TIME_SPAN_MINUTES);
	//	     t2 = times(&mend);
		     end=time(NULL);
		     printf("Secs of exec is %ld\n",end-start);

	             mysql_free_result(result3);
			
					    
		         
		  }
	   }
	}  
	  mysql_free_result(result);
			
	mysql_close(conn);
	
  return 0;
} /* End of main. */
