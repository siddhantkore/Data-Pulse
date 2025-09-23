The `application.properties`

#### Use Your Own Credentials
```
server.port=8080
spring.application.name=elastic
spring.servlet.multipart.max-file-size=10MB

spring.data.elasticsearch.cluster-name=my-doc-application
spring.data.elasticsearch.node-name=node-1
spring.elasticsearch.uris=http://localhost:9200

spring.data.mongodb.uri=mongodb://localhost:27017/documentsdb
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=documentsdb

s3.endpoint=http://localhost:9000
s3.bucket=my-bucket
s3.access-key=your_access_key
s3.secret-key=your_secret_key
s3.region=us-east-1

spring.main.allow-bean-definition-overriding=true

tesseract.datapath=/usr/share/tesseract-ocr/5/tessdata


openai.api.key=sk-proj-EYvAXLn*SQoCfjfdP5oW5fqLrCYTaADLYT3BlbkFJWSrNbyZTv5hYA3wZLD8*JsnEJGa4wM96x6l0kzkD8bwyYQPBv40Lc_02bRnxOwQA

openai.api.url=https://api.openai.com/v1/chat/completions
openai.model=gpt-4o-mini

# It's all Wrong Keys use your own !!!

gemini.api.key=AIza*SyDQdZuOex732qmXe72We3YqHKu92qu6jlFqJ*BjobpPU
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent


spring.mail.host=imap.gmail.com
spring.mail.port=993
spring.mail.username=m**2@gmail.com
spring.mail.password=m**k
spring.mail.properties.mail.imaps.ssl.enable=true
spring.mail.properties.mail.debug=true
```

Paste it in your application.properties
location : src/main/resources/application.properties