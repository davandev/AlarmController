package com.davan.alarmcontroller.http;

/*
 * #%L
 * NanoHttpd-Samples
 * %%
 * Copyright (C) 2012 - 2015 nanohttpd
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the nanohttpd nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Logger;

import com.davan.alarmcontroller.http.util.ServerRunner;
import com.davan.alarmcontroller.http.util.WakeUpReceiver;
public class WakeUpServer extends NanoHTTPD {

    /**
     * logger to log to.
     */
    private static final String CLASSNAME = WakeUpServer.class.getName();
    /**
     * Listener of received requests.
     */
    private WakeUpReceiver receiver;

    public static void main(String[] args) {
        ServerRunner.run(WakeUpServer.class);
    }

    public WakeUpServer(WakeUpReceiver callbackReceiver)
    {
        super(8080);
        receiver = callbackReceiver;
        try
        {
            printLocalAddress();
        }
        catch (IOException e)
        {
            Log.d(CLASSNAME,"Failed to retrieve ipaddress: "+ e.getMessage());
        }
    }


    public void printLocalAddress()throws IOException
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        Log.d(CLASSNAME,inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(CLASSNAME, "Exception:" + ex.toString());
        }
    }

    @Override
    public Response serve(IHTTPSession session)
    {
        Method method = session.getMethod();
        String uri = session.getUri();
        Log.d(CLASSNAME, method + " '" + uri + "' ");

        if(uri.compareTo("/WakeUp") == 0)
        {
            receiver.wakeup();
        }

        String msg = "<html><body><h1>Wake up</h1>\n";
        Map<String, String> parms = session.getParms();
        msg += "</body></html>\n";
        return newFixedLengthResponse(msg);
    }
}
