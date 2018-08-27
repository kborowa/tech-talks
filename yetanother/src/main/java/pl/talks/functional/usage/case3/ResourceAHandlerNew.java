package pl.talks.functional.usage.case3;

import io.micrometer.core.instrument.MeterRegistry;
import io.vavr.control.Option;
import lombok.extern.java.Log;
import pl.talks.functional.usage.case3.stub.Handler;
import pl.talks.functional.usage.case3.stub.ResourceA;
import pl.talks.functional.usage.case3.stub.ResourceAConverter;
import pl.talks.functional.usage.case3.stub.ResourceAValidator;
import pl.talks.functional.usage.case3.stub.ResourceFactoryClient;

@Log
public class ResourceAHandlerNew implements Handler {
    private final ResourceAConverter converter;
    private final ResourceFactoryClient resourceFactoryClient;
    private final MeterRegistry meterRegistry;
    private final ResourceAValidator resourceAValidator;

    public ResourceAHandlerNew(ResourceFactoryClient resourceFactoryClient, ResourceAConverter converter,
                               MeterRegistry meterRegistry, ResourceAValidator resourceAValidator) {
        this.resourceFactoryClient = resourceFactoryClient;
        this.converter = converter;
        this.meterRegistry = meterRegistry;
        this.resourceAValidator = resourceAValidator;
    }


    @Override
    public void handleMessage(ResourceA resourceAMessage) {
        Option.of(resourceAMessage)
                .filter(resourceAValidator::isValid)
                .onEmpty(() -> {
                    log.info("Invalid ResourceA Data");
                    meterRegistry.counter("messages.invalid.data").increment();
                })
                .peek(r -> meterRegistry.counter("messages.delivered").increment())
                .map(converter::convert)
                .peek(resourceFactoryClient::send);
    }

    @Override
    public Class<ResourceA> getHandlerClass() {
        return ResourceA.class;
    }
}