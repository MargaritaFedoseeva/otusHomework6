pipeline {
      agent { label 'java' }

      parameters {
        string(name: 'MOON_BASE_URL', defaultValue: 'ws://moon.moon.svc.cluster.local:4444', description: 'Адрес инстанса Moon')
        string(name: 'PLAYWRIGHT_VERSION', defaultValue: '1.62.0', description: 'Версия Playwright')
        booleanParam(name: 'HEADLESS', defaultValue: false, description: 'Запуск в headless режиме')
        string(name: 'WINDOW_SIZE', defaultValue: '1920,1080', description: 'Разрешение экрана')
    }

    stages {
        stage('Run Automation Tests') {
            steps {
                container('maven') {
                    withEnv([
                        "MOON_BASE_URL=${params.MOON_BASE_URL}",
                        "PLAYWRIGHT_VERSION=${params.PLAYWRIGHT_VERSION}",
                        "HEADLESS=${params.HEADLESS}",
                        "WINDOW_SIZE=${params.WINDOW_SIZE}"
                    ]) {
                        sh 'mvn clean test'
                        sh 'chmod -R 777 target/allure-results || true'
                    }
                }
            }
        }
    }

    post {
        always {
            allure ([results: [[path:'target/allure-results']]])
        }
    }
}