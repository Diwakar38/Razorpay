package com.project.razorpay.payment.simulator;

import com.project.razorpay.common.enums.ChaosMode;
import com.project.razorpay.common.enums.PaymentMethod;
import com.project.razorpay.common.enums.PaymentStatus;
import com.project.razorpay.common.util.RandomizerUtil;
import com.project.razorpay.payment.entity.Payment;
import com.project.razorpay.payment.repository.PaymentRepository;
import com.project.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankCallbackSimulator {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;

    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallbacks() {
        LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);

        List<Payment> candidate = paymentRepository
                .findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING, globalWindow);

        log.info("Simulating payments for {} payments", candidate.size());

        if(candidate.isEmpty()) return;

        for(Payment payment : candidate) {
            simulateCallback(payment);
        }
    }

    private void simulateCallback(Payment payment) {
        SimulatorConfig.MethodSimulatorConfig config = simulatorConfig.configFor(payment.getPaymentMethod());

        LocalDateTime dueAt = dueAt(payment, config);

        if(LocalDateTime.now().isBefore(dueAt)) return;

        ChaosMode chaosMode = simulatorConfig.getChaosMode();

        switch (chaosMode) {
            case SUCCESS -> resolve(payment, true);
            case FAILURE -> resolve(payment, false);
            case TIMEOUT -> {
                log.debug("BankCallback simulator: Payment Timeout");
            }
            case NORMAL, SLOW -> resolve(payment, shouldApprove(payment, config));
        }
    }

    private boolean shouldApprove(Payment payment, SimulatorConfig.MethodSimulatorConfig config) {
        int bucket = Math.abs(payment.getId().hashCode()) % 100;
        return bucket < config.getSuccessRate();
    }

    private void resolve(Payment payment, boolean approve) {
        if(approve) {
            String bankRef = "SIM_BANK_REF" + RandomizerUtil.randomBase64(8);
            paymentService.reslveAuthorization(payment.getId(), true, bankRef, null, null);
        } else {
            paymentService.reslveAuthorization(payment.getId(), false, null, "SIM_BANK_ERROR_CODE", "Simulated Bank Declined");
        }

    }

    private LocalDateTime dueAt(Payment payment, SimulatorConfig.MethodSimulatorConfig config) {
        int range = config.getMaxDelaySeconds() - config.getMinDelaySeconds();
        int delay = config.getMinDelaySeconds() + Math.abs(payment.getId().hashCode()) % (range + 1);

        if(simulatorConfig.getChaosMode() == ChaosMode.SLOW) {
            delay *= 2;
        }

        return payment.getCreatedAt().plusSeconds(delay);
    }

}
