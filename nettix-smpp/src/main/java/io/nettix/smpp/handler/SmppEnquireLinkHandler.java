

package io.nettix.smpp.handler;

import io.nettix.channel.HeartbeatFactory;
import io.nettix.channel.handler.HeartbeatHandler;
import io.nettix.smpp.pdu.EnquireLink;
import io.nettix.util.RoundRobinInteger;

/**
 * SMPP keep-alive message handler
 *
 * @author sanha
 */
public class SmppEnquireLinkHandler
    extends HeartbeatHandler<EnquireLink>
{
  /**
   * Constructor
   *
   * @param name
   *          name
   * @param delay
   *          send interval
   * @param timeout
   *          timeout
   * @param sequencer
   *          sequence generator
   */
  public SmppEnquireLinkHandler(String name, int delay, int timeout,
                                final RoundRobinInteger sequencer)
  {
    super(name, delay, timeout, new HeartbeatFactory<EnquireLink>()
    {
      @Override
      public EnquireLink createHeartbeat()
      {
        int sequence = sequencer.next();
        EnquireLink pdu = new EnquireLink();
        pdu.setSequence(sequence);
        return pdu;
      }
    });
  }

}
