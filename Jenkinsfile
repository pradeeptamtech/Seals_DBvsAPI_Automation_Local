pipeline { 
    agent  {

        label 'LFBigDataQA2'
    }
    
         
 
    stages
    {
        stage('Build') { 
            steps {   
                    
                        sh 'mvn clean compile'
                        echo 'Build Compile Successful'
                   }
             }
           stage('Testing Stage') { 
            steps {
                    //sh 'mvn test'
 sh 'mvn test -Dparam1=${serialNumbers}  -Dparam2=${productNumbers} -Dparam3=${startDate} -Dparam4=${endDate} -Dparam5=${eventCode} -Dparam6=${detectionDate} -Dparam7=${eventType}  -Dparam=${ApiName} ' 
                    echo 'Maven Test is Successful'
                    sh 'pwd'
               }
            }      
        
    }
   
post {                                 
        failure {
            emailext attachmentsPattern: '**/test-output/Report/test/ExtentReport.html', 
		body: '''See the attachment report with SealsDB comparision validation of Seals API. ''' , 
                   // subject: "FAIL- Mail is successful ", 
		subject: "${currentBuild.result}:- Status of pipeline Job and ${currentBuild.fullDisplayName} ",

		    mimeType: 'text/html',to: "${Addressee}"        //QA-Bigdata@groups.hp.com
            }
         success {
               emailext attachmentsPattern: '**/test-output/Report/test/ExtentReport.html', 
		body: '''See the attachment report with SealsDB Comparision validation of Seals API.  ''', 
                   // subject: "PASS- Mail is successful and ${currentBuild.fullDisplayName}",
		subject: " ${currentBuild.result}:- Status of pipeline Job and ${currentBuild.fullDisplayName}",

                    mimeType: 'text/html',to: "${Addressee}"       //yogesh.k@hp.com, aida.aranda.barbesa1@hp.com
             }             	        
    } 
 
}
