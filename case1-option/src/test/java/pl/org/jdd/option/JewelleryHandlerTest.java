package pl.org.jdd.option;

import io.micrometer.core.instrument.Metrics;
import io.vavr.control.Option;
import org.junit.Test;
import pl.org.jdd.legacy.stub.Location;
import pl.org.jdd.legacy.stub.Treasury;
import pl.org.jdd.legacy.stub.jewellery.Jewellery;
import pl.org.jdd.legacy.stub.jewellery.JewelleryPacker;
import pl.org.jdd.legacy.stub.jewellery.JewelleryValidator;

public class JewelleryHandlerTest {

  private final Handler<Jewellery, Location> handler =
      new JewelleryHandler(new JewelleryValidator(), new JewelleryPacker(), new Treasury(), Metrics.globalRegistry);

  @Test
  public void topazIsNotAJewelleryForTola() throws Exception {
    Option<Location> location = handler.handleSouvenir(new Jewellery("topaz"));

    assert location.isEmpty();
  }

  @Test
  public void diamondsAreLovedByTolaAndPackedInSafe() throws Exception {
    Location expectedLocation = new Location("safe behind the picture");

    Option<Location> location = handler.handleSouvenir(new Jewellery("diamonds"));

    assert location.get().equals(expectedLocation);
  }
}