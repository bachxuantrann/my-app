package bachtx.myapp.sso_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String AUDIT_TOPIC = "sso-audit-events";

    @Bean
    public NewTopic auditTopic() {
        return TopicBuilder.name(AUDIT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
