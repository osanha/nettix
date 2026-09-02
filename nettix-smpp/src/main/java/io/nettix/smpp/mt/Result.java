

package io.nettix.smpp.mt;

/**
 * Delivery receipt parse result
 *
 * @author sanha
 */
public class Result
{
  /**
   * SMS ID
   */
  private String _msgId;

  /**
   * State string
   */
  private String _state;

  /**
   * Constructor
   *
   * @param msgId
   *          SMS ID
   * @param state
   *          state string
   */
  public Result(String msgId, String state)
  {
    _msgId = msgId;
    _state = state;
  }

  /**
   * Returns SMS ID
   *
   * @return SMS ID
   */
  public String msgId()
  {
    return _msgId;
  }

  /**
   * Returns state string
   *
   * @return state string
   */
  public String state()
  {
    return _state;
  }

}
