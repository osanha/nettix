

package io.nettix.smpp;

import java.util.HashMap;
import java.util.Map;

/**
 * SMPP meta class
 *
 * @author sanha
 */
public class SMPP
{
  /**
   * Map with command_status as key
   */
  private static final Map<Integer, SmppStatus> _statusMap = new HashMap<Integer, SmppStatus>();

  /**
   * Map with command_id as key
   */
  private static final Map<Integer, SmppCommand> _commandMap = new HashMap<Integer, SmppCommand>();

  /**
   * Map with option_key as key
   */
  private static final Map<Integer, SmppOption> _optionMap = new HashMap<Integer, SmppOption>();

  static
    {
      for (SmppStatus status : SmppStatus.values())
        _statusMap.put(status.value(), status);

      for (SmppCommand command : SmppCommand.values())
        _commandMap.put(command.value(), command);

      for (SmppOption option : SmppOption.values())
        _optionMap.put(option.value(), option);
    }

  /**
   * Returns the validity period string.
   *
   * @param sec
   *          total time (in seconds)
   * @return formatted string
   */
  public static String getValidityPeriod(int sec)
  {
    int min = 0;
    int hour = 0;
    int day = 0;

    if (sec >= 86400)
      {
        day = sec / 86400;
        sec = sec % 86400;
      }

    if (sec >= 3600)
      {
        hour = sec / 3600;
        sec = sec % 3600;
      }

    if (sec >= 60)
      {
        min = sec / 60;
        sec = sec % 60;
      }

    return String.format("0000%02d%02d%02d%02d000R", day, hour, min, sec);
  }

  /**
   * Returns the status object corresponding to the code value.
   *
   * @param code
   *          status code
   * @return status object
   * @throws IllegalArgumentException
   */
  public static SmppStatus getStatus(int code) throws IllegalArgumentException
  {
    SmppStatus status = _statusMap.get(code);

    if (status == null)
      throw new IllegalArgumentException("Invalid command_status: " + code);

    return status;
  }

  /**
   * Returns the command object corresponding to the code value.
   *
   * @param code
   *          command_id code
   * @return command object
   * @throws IllegalArgumentException
   */
  public static SmppCommand getCommand(int code)
      throws IllegalArgumentException
  {
    SmppCommand command = _commandMap.get(code);

    if (command == null)
      throw new IllegalArgumentException("Invalid command_id: " + code);

    return command;
  }

  /**
   * Returns the option object corresponding to the code value.
   *
   * @param code
   *          option code
   * @return option object
   */
  public static SmppOption getOption(int code)
  {
    SmppOption option = _optionMap.get(code);

    if (option == null)
      {
        option = SmppOption.UNKNOWN;
        option.value(code);
      }

    return option;
  }

}
