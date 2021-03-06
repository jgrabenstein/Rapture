/**
 * Copyright (C) 2011-2015 Incapture Technologies LLC
 *
 * This is an autogenerated license statement. When copyright notices appear below
 * this one that copyright supercedes this statement.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 *
 * Unless explicit permission obtained in writing this software cannot be distributed.
 */
package rapture.kernel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import rapture.common.BlobContainer;
import rapture.common.CallingContext;
import rapture.common.SeriesPoint;
import rapture.common.client.HttpBlobApi;
import rapture.common.client.HttpDocApi;
import rapture.common.client.HttpLoginApi;
import rapture.common.client.HttpSeriesApi;
import rapture.common.client.SimpleCredentialsProvider;
import rapture.common.impl.jackson.JacksonUtil;

public class UriDocPathTest {

    private CallingContext ctx = ContextFactory.getKernelUser();
    private CallingContext localCtx = null;
    private HttpDocApi docApi;
    private HttpSeriesApi seriesApi;
    private HttpBlobApi blobApi;
    private String authority = "respect";

    String username = "qqqq";
    String password = "qqqq";
    String url = "http://localhost:8665/rapture";

    @Before
    public void setup() {

        System.out.println("Logging in...");
        HttpLoginApi login = new HttpLoginApi(url, new SimpleCredentialsProvider(username, password));
        login.login();
        System.out.println("Logged in");
        docApi = new HttpDocApi(login);
        seriesApi = new HttpSeriesApi(login);
        blobApi = new HttpBlobApi(login);

        if (!docApi.docRepoExists(authority)) {
            docApi.createDocRepo(authority, "NREP {} USING MONGODB {prefix=\"" + authority + "\"}");
        }
        if (!seriesApi.seriesRepoExists(authority)) {
            seriesApi.createSeriesRepo(authority, "SREP {} USING CASSANDRA {keyspace=\"" + authority + "\", cf=\"series\"" + authority + "\"}");
        }
        if (!blobApi.blobRepoExists(authority)) {
            blobApi.createBlobRepo(authority, "BLOB {} USING MONGODB { grid=\"" + authority + "\" }", "REP {} USING MONGODB {prefix=\"" + authority + "\"}");
        }
    }

    @After
    public void teardown() {
        if (docApi.docRepoExists(authority)) {
            docApi.deleteDocRepo(authority);
        }
        if (seriesApi.seriesRepoExists(authority)) {
            seriesApi.deleteSeriesRepo(authority);
        }
        if (blobApi.blobRepoExists(authority)) {
            blobApi.deleteBlobRepo(authority);
        }
    }

    @Test
    public void testDocPath() {
        Map<String, String> foo = new HashMap<>();
        foo.put("X", "Y");
        String fooStr = JacksonUtil.jsonFromObject(foo);

        // Ensure that all ASCII characters are legal in DocPath unless explicitly prohibited.

        for (int i = 32; i < 128; i++) {
            char c = (char) i;
            String uri = "//" + authority + "/folder/docu" + c + "ment/path/foo";
            System.out.println(uri);
            switch (c) {
                case '+': // We currently do not allow + as a substitute for space
                case ':':
                case '&':
                case '?':
                case ' ':
                case '`':
                case '\'':
                case ',':
                case '}':
                case '[':
                case '<':
                case ';':
                case '^':
                case '{':
                case '>':
                case ']':
                case '=':
                case '\\':
                    try {
                        docApi.putDoc(uri, fooStr);
                        fail("Character " + c + " should not be legal in DocPath");
                    } catch (Exception e1) {
                        System.out.println("Character " + c + " is not legal in DocPath");
                    }
                    break;

                // These are legal, but have meaning so can't be used in DocPath
                case '$':
                case '#':
                case '@':
                    break;

                default: // Everything else should be valid
                    docApi.putDoc(uri, fooStr);
                    String bar = docApi.getDoc(uri);
                    assertEquals(fooStr, bar);

                    seriesApi.addDoublesToSeries(uri, ImmutableList.of("A", "B", "C"), ImmutableList.of(1.0D, 2.0D, 3.0D));
                    List<SeriesPoint> list = seriesApi.getPoints(uri);
                    double d = 1.0D;
                    for (SeriesPoint val : list) {
                        assertEquals("" + d++, val.getValue());
                    }

                    blobApi.putBlob(uri, new byte[0], "text/plain");

                    byte[] b1 = fooStr.getBytes();
                    blobApi.addBlobContent(uri, b1);
                    BlobContainer theBlackLagoon = blobApi.getBlob(uri);
                    byte[] b2 = theBlackLagoon.getContent();
                    assertEquals(b1.length, b2.length);
                    for (int j = 0; j < b1.length; j++) {
                        assertEquals(b1[j], b2[j]);
                    }

            }
        }
    }
    
    @Test
    public void testAuthority() {
        Map<String, String> foo = new HashMap<>();
        foo.put("X", "Y");
        String fooStr = JacksonUtil.jsonFromObject(foo);

        // Ensure that all ASCII characters are legal in DocPath unless explicitly prohibited.

        for (int i = 32; i < 128; i++) {
            char c = (char) i;
            String authVariable = "auth" + c + "ority";

            String uri = "//" + authVariable + "/folder/document/path/foo";

            System.out.println(uri);
            switch (c) {
                case '+': // We currently do not allow + as a substitute for space
                          // see below for ':'
                case '&':
                case '?':
                case ' ':
                case '`':
                case '\'':
                case ',':
                case '}':
                case '[':
                case '<':
                case ';':
                case '^':
                case '{':
                case '>':
                case ']':
                case '=':
                case '\\':
                    try {
                        docApi.putDoc(uri, fooStr);
                        fail("Character " + c + " should not be legal in Authority");
                    } catch (Exception e1) {
                        System.out.println("Character " + c + " is not legal in Authority");
                    }
                    break;

                // Deviation from DocPath
                case '/':   // considered legal but doesn't work - because ://
                case ':':   // considered legal but doesn't work - because ://
                case '#':   // illegal
                case '$':   // illegal
                    break;
                    
                case '@':   // legal and works
                case '"':   // Is part of the whitelist but causes a problem with antlr. (Bug?)
                    
                default: // Everything else should be valid
                    
                    if (!docApi.docRepoExists(authVariable)) {
                        docApi.createDocRepo(authVariable, "NREP {} USING MONGODB {prefix=\"" + authority + "\"}");
                    }
                    if (!seriesApi.seriesRepoExists(authVariable)) {
                        seriesApi.createSeriesRepo(authVariable, "SREP {} USING CASSANDRA {keyspace=\"" + authority + "\", cf=\"series\"" + authority
                                + "\"}");
                    }
                    if (!blobApi.blobRepoExists(authVariable)) {
                        blobApi.createBlobRepo(authVariable, "BLOB {} USING MONGODB { grid=\"" + authority + "\" }", "REP {} USING MONGODB {prefix=\""
                                + authVariable + "\"}");
                    }

                    docApi.putDoc(uri, fooStr);
                    String bar = docApi.getDoc(uri);
                    assertEquals(fooStr, bar);

                    seriesApi.addDoublesToSeries(uri, ImmutableList.of("A", "B", "C"), ImmutableList.of(1.0D, 2.0D, 3.0D));
                    List<SeriesPoint> list = seriesApi.getPoints(uri);
                    double d = 1.0D;
                    for (SeriesPoint val : list) {
                        assertEquals("" + d++, val.getValue());
                    }

                    blobApi.putBlob(uri, new byte[0], "text/plain");

                    byte[] b1 = fooStr.getBytes();
                    blobApi.addBlobContent(uri, b1);
                    BlobContainer theBlackLagoon = blobApi.getBlob(uri);
                    byte[] b2 = theBlackLagoon.getContent();
                    assertEquals(b1.length, b2.length);
                    for (int j = 0; j < b1.length; j++) {
                        assertEquals(b1[j], b2[j]);
                    }

                    if (docApi.docRepoExists(authVariable)) {
                        docApi.deleteDocRepo(authVariable);
                    }
                    if (seriesApi.seriesRepoExists(authVariable)) {
                        seriesApi.deleteSeriesRepo(authVariable);
                    }
                    if (blobApi.blobRepoExists(authVariable)) {
                        blobApi.deleteBlobRepo(authVariable);
                    }
            }
        }
    }
}
