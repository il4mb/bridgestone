package com.il4mb.bridgestone.app.api;

/**
 * ini adalah object respon dari api server v1
 * @var int code   - code respon dari server
 * @var int status - indikasi status server
 * @var String message    - pesan dari server
 * @var Object data - response data dari server
 */
public class ResponseModel {
    public int code       = 0;
    public int status     = 200;
    public String message = "Null";
    public Object data    = null;

}


