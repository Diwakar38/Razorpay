package com.project.razorpay.payment.simulator;

import com.project.razorpay.common.enums.ChaosMode;
import com.project.razorpay.common.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "payment.simulator")
@Setter
@Getter
public class SimulatorConfig {

    private Integer pollIntegerMs = 2000;

    private ChaosMode chaosMode = ChaosMode.NORMAL;

    private Map<String, MethodSimulatorConfig> methods = new HashMap<>();

    public MethodSimulatorConfig configFor(PaymentMethod method) {
        return methods.getOrDefault(method.name(),new MethodSimulatorConfig());
    }

    @Getter
    @Setter
    public static class MethodSimulatorConfig {
        private Integer minDelaySeconds = 1;
        private Integer maxDelaySeconds = 5;
        private Integer successRate = 80;
    }

}
