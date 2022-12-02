#!groovy​
withEnv(['channel=D044QHWTS23','DB_ENGINE=sqlite']) {
    stage("Intro"){
            node {
                sh "echo 'Hola'"
            }
    }

    try{

        if (env.BRANCH_NAME =~ ".*release/.*" || env.BRANCH_NAME =~ ".*feature/.*") {
            stage("Paso 1: Compliar"){
                node {
                    sh "echo 'Compile Code! oriverhu'"
                    sh "./mvnw clean compile -e"
                }
            }
            stage("Paso 2: Testear"){
                node {
                    script {
                    sh "echo 'Test Code!'"
                    // Run Maven on a Unix agent.
                    sh "./mvnw clean test -e"
                    }
                }
            }
            stage("Paso 3: Build .Jar"){
                node {
                        script {
                        sh "echo 'Build .Jar!'"
                        // Run Maven on a Unix agent.
                        sh "./mvnw  clean package -e"
                        }            
                }
            }
            stage("Paso 4: Análisis SonarQube"){
                node {
                    withSonarQubeEnv('sonarqube') {
                        sh "echo 'Calling sonar Service in another docker container!'"
                        // Run Maven on a Unix agent to execute Sonar.
                        sh './mnw clean verify sonar:sonar -Dsonar.projectKey=ms-iclab-grupo2 -Dsonar.projectName=ms-iclab-grupo2 -Dsonar.java.binaries=build'
                    }
                }
            } 
            stage("Paso 5: Merge"){
                node {
                    
                }
            }
            stage("Paso 5: Merge"){
                node {
                    slackSend color: 'good', channel: "${env.channel}", message: "[grupo2] [${env.JOB_NAME}] [${BUILD_TAG}] Se finaliza [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'token-slack'
                }
            }

            
                   
        }


        if (env.BRANCH_NAME =~ ".*main" || env.BRANCH_NAME =~  ".*develop") {
            stage("CD"){
                node {
                    sh "echo 'DESPLIEGUE'"
                }
            }  
            stage("Paso 1: Subir Artefacto a Nexus"){
                    node {
                            nexusPublisher nexusInstanceId: 'nexus',
                                nexusRepositoryId: 'repository_grupo2',
                                packages: [
                                    [$class: 'MavenPackage',
                                        mavenAssetList: [
                                            [classifier: '',
                                            extension: 'jar',
                                            filePath: 'build/DevOpsUsach2020-0.0.1.jar'
                                        ]
                                    ],
                                        mavenCoordinate: [
                                            artifactId: 'DevOpsUsach2020',
                                            groupId: 'com.devopsusach2020',
                                            packaging: 'jar',
                                            version: '0.0.1'
                                        ]
                                    ]
                                ]                
                    }
                }
                stage("Paso 2: Descargar Nexus"){
                    node {
                            sh ' curl -X GET -u admin:$NEXUS_PASSWORD "http://nexus:8081/repository/maven-usach-ceres/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar" -O'
                    }
                }
                stage("Paso 3: Levantar Artefacto Jar en server Jenkins"){
                    node {
                            sh 'nohup java -jar DevOpsUsach2020-0.0.1.jar & >/dev/null'                
                    }
                }
                stage("Paso 4: Testear Artefacto - Dormir(Esperar 20sg) "){
                    node {
                            sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"                
                    }
                }
                stage("Paso 5: Detener Atefacto jar en Jenkins server"){
                    node {
                        sh '''
                            echo 'Process Java .jar: ' $(pidof java | awk '{print $1}')  
                            sleep 20
                            kill -9 $(pidof java | awk '{print $1}')
                        '''
                    }
                }        
                
        }
            
    }

    catch (e) {
            echo 'This will run only if failed'
            slackSend color: 'danger', channel: "${env.channel}", message: "[grupo2] [${env.JOB_NAME}] [${BUILD_TAG}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'token-slack'
            throw e

    }
}

 
