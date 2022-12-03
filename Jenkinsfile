#!groovy​
withEnv(['channel=C04B17VE0JH','DB_ENGINE=sqlite']) {
    stage("Intro"){
            node {
                sh "echo 'Hola'"
            }
    }

    def BRANCH = "${env.BRANCH_NAME.split("/")[1]}"
    def BRANCH_TYPE = "${env.BRANCH_NAME.split("/")[0]}"

    try{

            stage("CI 1: Compliar"){
                node {
                    sh "echo 'Compile Code! oriverhu'"
                    sh "./mvnw clean compile -e"
                }
            }
            stage("CI 2: Testear"){
                node {
                    script {
                        sh "echo 'Test Code!'"
                        // Run Maven on a Unix agent.
                        sh "./mvnw clean test -e"
                    }
                }
            }
            stage("CI 3: Build .Jar"){
                node {
                    script {
                        sh "echo 'Build .Jar!'"
                        // Run Maven on a Unix agent.
                        sh "./mvnw  clean package -e"
                    }            
                }
            }
            stage("CI 4: Análisis SonarQube"){
                node {
                    withSonarQubeEnv('sonarqube') {
                        sh "echo 'Calling sonar Service in another docker container!'"
                        // Run Maven on a Unix agent to execute Sonar.
                        sh './mvnw clean verify sonar:sonar -Dsonar.projectKey=ms-iclab-grupo2 -Dsonar.projectName=ms-iclab-grupo2 -Dsonar.java.binaries=build'
                    }
                }
            } 


        if (env.BRANCH_NAME =~ ".*release/.*") {
            

            stage("CD"){
                node {
                    sh "echo 'artefacto....'"
                }
            }  
            stage("CD 1: Subir Artefacto a Nexus"){
                    node {
                        nexusPublisher nexusInstanceId: 'nexus',
                            nexusRepositoryId: 'repository_grupo2',
                            packages: [
                                [$class: 'MavenPackage',
                                    mavenAssetList: [
                                        [classifier: '',
                                        extension: 'jar',
                                        filePath: 'build/DevOpsUsach2020-$BRANCH.jar'
                                    ]
                                ],
                                    mavenCoordinate: [
                                        artifactId: 'DevOpsUsach2020',
                                        groupId: 'com.devopsusach2020',
                                        packaging: 'jar',
                                        version: '$BRANCH'
                                    ]
                                ]
                            ]                
                    }
                }
                stage("CD 2: Descargar Nexus"){
                    node {
                            sh ' curl -X GET -u admin:$NEXUS_PASSWORD "http://nexus:8081/repository/maven-usach-ceres/com/devopsusach2020/DevOpsUsach2020/$BRANCH/DevOpsUsach2020-$BRANCH.jar" -O'
                    }
                }
                stage("CD 3: Levantar Artefacto Jar en server Jenkins"){
                    node {
                            sh 'nohup java -jar DevOpsUsach2020-$BRANCH.jar & >/dev/null'                
                    }
                }
                stage("CD 4: Testear Artefacto - Dormir(Esperar 20sg) "){
                    node {
                            sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"                
                    }
                }
                stage("CD 5: Detener Atefacto jar en Jenkins server"){
                    node {
                        sh '''
                            echo 'Process Java .jar: ' $(pidof java | awk '{print $1}')  
                            sleep 20
                            kill -9 $(pidof java | awk '{print $1}')
                        '''
                    }
                }
                
        }


        stage("Mensaje slack"){
            node {
                slackSend (color: 'good', channel: "${env.channel}", message: "[grupo2] [${env.JOB_NAME}] [${BUILD_NUMBER}] Despliegue Exitoso [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'token-slack')
            }
        }        
            
    }

    catch (e) {
            echo 'This will run only if failed'
            slackSend (color: 'danger', channel: "${env.channel}", message: "[grupo2] [${env.JOB_NAME}] [${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'token-slack')
            throw e
    }
}

 
