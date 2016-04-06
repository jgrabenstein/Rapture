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
package reflex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rapture.common.api.ScriptingApi;
import rapture.common.client.CredentialsProvider;
import rapture.common.client.HttpLoginApi;
import rapture.common.client.ScriptClient;
import rapture.common.client.SimpleCredentialsProvider;
import reflex.IReflexHandler;
import reflex.ReflexParser;
import reflex.ReflexTreeWalker;
import reflex.ResourceBasedTest;
import reflex.StandardReflexHandler;

public class AsyncTest extends ResourceBasedTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

	}

	protected ReflexTreeWalker createReflexTreeWalker(CommonTreeNodeStream nodes, ReflexParser parser) {
		ReflexTreeWalker treeWalker = new ReflexTreeWalker(nodes, parser.languageRegistry);
		String hostUrl = "http://localhost:8665/rapture";
		CredentialsProvider provider = new SimpleCredentialsProvider("rapture", "rapture");
		HttpLoginApi loginApi = new HttpLoginApi(hostUrl, provider);
		loginApi.login();
		ScriptingApi api = new ScriptClient(loginApi);
		IReflexHandler handler = new StandardReflexHandler(api);
		treeWalker.setReflexHandler(handler);
		return treeWalker;
	}

	@Test
	public void runCallAndSuspendTest() throws RecognitionException {
		runTestFor("/async/callAndSuspend.rfx");
	}

	@Test
	public void runCallAndSuspendSingleTest() throws RecognitionException {
		runTestFor("/async/callAndSuspendSingle.rfx");
	}

	@Test
	public void runCallAndWaitTest() throws RecognitionException {
		runTestFor("/async/callAndWait.rfx");
	}

	@Test
	public void runCallOnServerAndWaitTest() throws RecognitionException {
		runTestFor("/async/callOnServerAndWait.rfx");
	}

	@Test
	public void RAP3500() throws RecognitionException {
		String output = runTestFor("/async/RAP3500.rfx");
		assertNotNull(output);
		System.out.println(output);
		assertTrue(output.contains("Unexpected identifier at token Wibble  at line 1"));
	}

	@Test
	public void RAP3502() throws RecognitionException {
		String output = runTestFor("/async/RAP3502.rfx");
		assertNotNull(output);
		System.out.println(output);
//		assertTrue(output.contains("Unexpected identifier at token Wibble  at line 1"));
	}

	@Test
	public void RAP3504() throws RecognitionException {
		String output = runTestFor("/async/RAP3504.rfx");
		assertNotNull(output);
		System.out.println(output);
		String lines[] = output.split("\n");
		assertTrue(lines[1].endsWith("I am the target script"));
		assertEquals("}, status=FINISHED, CLASS=rapture.common.dp.WorkOrderStatus}", lines[2]);
	}
}
