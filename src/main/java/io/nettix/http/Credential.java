package io.nettix.http;

/**
 * Authentication credentials set in the received request message.
 *
 * @author sanha
 */
public class Credential
{
    /**
     * User ID
     */
    private String id;

    /**
     * Password
     */
    private String pw;

    /**
     * Constructor.
     *
     * @param id
     *          User ID
     * @param pw
     *          Password
     */
    public Credential(String id, String pw)
    {
        this.id = id;
        this.pw = pw;
    }

    /**
     * Sets the user ID.
     *
     * @param id
     *          User ID
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Sets the password.
     *
     * @param pw
     *          Password
     */
    public void setPw(String pw)
    {
        this.pw = pw;
    }

    /**
     * Gets the user ID.
     *
     * @return User ID
     */
    public String getId()
    {
        return id;
    }

    /**
     * Gets the password.
     *
     * @return Password
     */
    public String getPw()
    {
        return pw;
    }
}