# spring_msa_myselectshop

Date: 2024.12

Goal: imporve the previous project(spring_myselectshop) by applying MSA(Spring Cloud) and Message Queue(Kafka)

Dev Environment:
- Framework: Spring Boot, Spring Cloud(API Gateway, Eureka, FeignClinet)
- DB: Postgresql(RDB), Redis(In-memory Cache)
- A&A: Spring Security (JWT, Filter)
- API: RestTemplate(Naver Open API)
- Infra: Docker(Docker Compose), Kafka, Multi Module

Features:
- Search pruduct information via Naver API
- Save my products and their prices
- Set my target prices
- Categorize my products into my folders
- Update their price (Kafka Message Queue)
- Update their price regulary (Scheduler)

![image](https://github.com/user-attachments/assets/97afa0a1-55dd-45de-ac44-e4071c12baa0)

