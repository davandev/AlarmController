package com.davan.alarmcontroller.settings;

/**
 * Created by davandev on 2016-05-10.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.davan.alarmcontroller.R;

public class License extends Activity
{
    private static final String TAG = License.class.getSimpleName();
    private String divider = "\n\n==============================\n3rd Party Licenses\n==============================\n\n";
    private String mit_license =
        " - ZenitGatekeeper -\n"+
        "\n"+
        "Copyright (c) 2017 davandev\n"+
        "\n"+
        "Permission is hereby granted, free of charge, to any person obtaining a copy\n"+
        "of this software and associated documentation files (the \"Software\"), to deal\n"+
        "in the Software without restriction, including without limitation the rights\n"+
        "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n"+
        "copies of the Software, and to permit persons to whom the Software is\n"+
        "furnished to do so, subject to the following conditions:\n"+
        "\n"+
        "The above copyright notice and this permission notice shall be included in all\n"+
        "copies or substantial portions of the Software.\n"+
        "\n"+
        "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n"+
        "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n"+
        "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n"+
        "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n"+
        "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n"+
        "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n"+
        "SOFTWARE.";

    private String nanohttpdLicense =
            "NanoHttpd-Webserver\n" +
            "\n" +
            "Copyright (C) 2012 - 2015 nanohttpd\n" +
            "\n" +
            "Redistribution and use in source and binary forms, with or without modification,\n" +
            "are permitted provided that the following conditions are met:\n" +
            "\n" +
            "1. Redistributions of source code must retain the above copyright notice, this\n" +
            "   list of conditions and the following disclaimer.\n" +
            "\n" +
            "2. Redistributions in binary form must reproduce the above copyright notice,\n" +
            "   this list of conditions and the following disclaimer in the documentation\n" +
            "   and/or other materials provided with the distribution.\n" +
            "\n" +
            "3. Neither the name of the nanohttpd nor the names of its contributors\n" +
            "   may be used to endorse or promote products derived from this software without\n" +
            "   specific prior written permission.\n" +
            "\n" +
            "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND\n" +
            "ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED\n" +
            "WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\n" +
            "IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,\n" +
            "INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,\n" +
            "BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\n" +
            "DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF\n" +
            "LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE\n" +
            "OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED\n" +
            "OF THE POSSIBILITY OF SUCH DAMAGE.\n";

    private StringBuilder log;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        TextView tv = (TextView)findViewById(R.id.textView1);
        String license = mit_license + divider + nanohttpdLicense;
        tv.setText(license);

    }
}