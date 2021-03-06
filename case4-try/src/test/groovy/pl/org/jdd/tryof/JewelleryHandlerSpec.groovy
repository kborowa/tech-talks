package pl.org.jdd.tryof

import io.micrometer.core.instrument.Metrics
import io.vavr.control.Either
import pl.org.jdd.legacy.stub.Location
import pl.org.jdd.legacy.stub.Treasury
import pl.org.jdd.legacy.stub.jewellery.Jewellery
import pl.org.jdd.legacy.stub.jewellery.JewelleryPacker
import pl.org.jdd.legacy.stub.jewellery.JewelleryValidator
import pl.org.jdd.tryof.failure.ConstraintTypes
import pl.org.jdd.tryof.failure.Failure
import spock.lang.Specification
import spock.lang.Unroll

class JewelleryHandlerSpec extends Specification {

    @Unroll
    def "for #jewellery Handler returns #expectedEither"() {
        setup:
        def validator = new JewelleryValidator()
        def packer = new JewelleryPacker()
        def treasury = new Treasury()
        def meterRegistry = Metrics.globalRegistry
        def handler = new JewelleryHandler(validator, packer, treasury, meterRegistry)

        when:
        def either = handler.handleSouvenir(jewellery)

        then:
        either == expectedEither

        where:
        jewellery                 || expectedEither
        new Jewellery("topaz")    || Either.left(Failure.create(ConstraintTypes.NOT_VALUABLE_SOUVENIR))
        null                      || Either.left(Failure.create(ConstraintTypes.SOMETHING_IS_NOT_DEFINED))
        new Jewellery("diamonds") || Either.right(new Location("safe behind the picture"))
    }
}
