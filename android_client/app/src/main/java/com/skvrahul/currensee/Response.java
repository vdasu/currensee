package com.skvrahul.currensee;

/**
 * Created by skvrahul on 23/1/18.
 */

public class Response
{

    private String status;

    private String total;

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getTotal ()
    {
        return total;
    }

    public void setTotal(String total)
    {
        this.total = total;
    }

    @Override
    public String toString()
    {
        return "Response [status = "+status+", total = "+total+"]";
    }
}




