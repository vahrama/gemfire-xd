/*
 * Copyright (c) 2010-2015 Pivotal Software, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.gemstone.gemfire.internal.cache.tier.sockets;

import hydra.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.gemstone.gemfire.cache.AttributesFactory;
import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.DataPolicy;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionAttributes;
import com.gemstone.gemfire.cache.Scope;
import com.gemstone.gemfire.cache.util.BridgeServer;
import com.gemstone.gemfire.distributed.DistributedSystem;
import com.gemstone.gemfire.distributed.internal.DistributionConfig;
import com.gemstone.gemfire.internal.AvailablePort;
import com.gemstone.gemfire.internal.cache.BridgeServerImpl;
import com.gemstone.gemfire.internal.cache.ha.HARegionQueue;
import com.gemstone.gemfire.cache.client.*;

import dunit.DistributedTestCase;
import dunit.Host;
import dunit.VM;

/**
 * This tests the fix for bug 73210. Reason for the bug was that HARegionQueue's
 * destroy was not being called on CacheClientProxy's closure. As a result,
 * stats were left open.
 * 
 * @author Dinesh Patel
 * 
 */
public class Bug37210DUnitTest extends DistributedTestCase
{
  /** the cache server */
  VM server = null;

  /** the cache client */
  VM client = null;

  /** the cache */
  private static Cache cache = null;

  /** port for the cache server */
  private static int PORT;

  /** name of the test region */
  private static final String REGION_NAME = "Bug37210DUnitTest_Region";

  /**
   * Constructor
   * 
   * @param name -
   *                name for this test instance
   */
  public Bug37210DUnitTest(String name) {
    super(name);
  }

  /**
   * Creates the cache server and sets the port
   * 
   * @throws Exception -
   *                 thrown if any problem occurs in initializing the test
   */
  public void setUp() throws Exception
  {
    super.setUp();
    final Host host = Host.getHost(0);
    server = host.getVM(0);
    client = host.getVM(2);
    PORT = ((Integer)server
        .invoke(Bug37210DUnitTest.class, "createServerCache")).intValue();
  }

  /**
   * Create the cache
   * 
   * @param props -
   *                properties for DS
   * @return the cache instance
   * @throws Exception -
   *                 thrown if any problem occurs in cache creation
   */
  private Cache createCache(Properties props) throws Exception
  {
    DistributedSystem ds = getSystem(props);
    ds.disconnect();
    ds = getSystem(props);
    Cache cache = null;
    cache = CacheFactory.create(ds);
    if (cache == null) {
      throw new Exception("CacheFactory.create() returned null ");
    }
    return cache;
  }

  /**
   * close the cache instances in server and client during tearDown
   * 
   * @throws Exception
   *                 thrown if any problem occurs in closing cache
   */
  public void tearDown2() throws Exception
  {
    super.tearDown2();
    // close client
    client.invoke(Bug37210DUnitTest.class, "closeCache");

    // close server
    server.invoke(Bug37210DUnitTest.class, "closeCache");
  }

  /**
   * This test does the following:<br>
   * 1)Create the client<br>
   * 2)Do some operations from the cache-server<br>
   * 3)Stop the primary cache-server<br>
   * 4)Explicity close the CacheClientProxy on the server. <br>
   * 5)Verify that HARegionQueue stats are closed and entry for the haregion is
   * removed from dispatchedMessagesMap.
   * 
   * @throws Exception -
   *                 thrown if any problem occurs in test execution
   */
  public void testHAStatsCleanup() throws Exception
  {
    Log.getLogWriter().info("testHAStatsCleanup : BEGIN");
    client.invoke(Bug37210DUnitTest.class, "createClientCache",
        new Object[] { getServerHostName(Host.getHost(0)), new Integer(PORT) });
    server.invoke(Bug37210DUnitTest.class, "doEntryOperations");
    server.invoke(Bug37210DUnitTest.class,
        "closeCacheClientProxyAndVerifyStats");
    Log.getLogWriter().info("testHAStatsCleanup : END");
  }

  /**
   * Creates and starts the cache-server
   * 
   * @return - the port on which cache-server is running
   * @throws Exception -
   *                 thrown if any problem occurs in cache/server creation
   */
  public static Integer createServerCache() throws Exception
  {
    Bug37210DUnitTest test = new Bug37210DUnitTest("temp");
    Properties props = new Properties();
    cache = test.createCache(props);
    AttributesFactory factory = new AttributesFactory();
    factory.setScope(Scope.DISTRIBUTED_ACK);
    factory.setDataPolicy(DataPolicy.REPLICATE);

    RegionAttributes attrs = factory.create();

    cache.createRegion(REGION_NAME, attrs);
    BridgeServer server = cache.addBridgeServer();
    int port = AvailablePort.getRandomAvailablePort(AvailablePort.SOCKET);
    server.setPort(port);
    server.setNotifyBySubscription(false);
    server.setSocketBufferSize(32768);
    server.setMaximumTimeBetweenPings(1000000);
    server.start();
    Log.getLogWriter().info("Server started at PORT = " + port);
    return new Integer(server.getPort());
  }

  /**
   * Initializes the cache client
   * 
   * @param port -
   *                port for the primary cache-server
   * 
   * @throws Exception-thrown
   *                 if any problem occurs in initializing the client
   */
  public static void createClientCache(String host, Integer port) throws Exception
  {
    Bug37210DUnitTest test = new Bug37210DUnitTest("temp");
    Properties props = new Properties();
    props.setProperty(DistributionConfig.MCAST_PORT_NAME, "0");
    props.setProperty(DistributionConfig.LOCATORS_NAME, "");
    cache = test.createCache(props);
    Pool p = PoolManager.createFactory()
      .addServer(host, port.intValue())
      .setSubscriptionEnabled(true)
      .setThreadLocalConnections(true)
      .setReadTimeout(10000)
      .setSocketBufferSize(32768)
      .setMinConnections(3)
      .setSubscriptionRedundancy(-1)
      // .setRetryInterval(10000)
      // .setRetryAttempts(5)
      .create("Bug37210UnitTestPool");
    AttributesFactory factory = new AttributesFactory();
    factory.setScope(Scope.DISTRIBUTED_ACK);

    factory.setPoolName(p.getName());

    RegionAttributes attrs = factory.create();
    Region region = cache.createRegion(REGION_NAME, attrs);
    region.registerInterest("ALL_KEYS");
  }

  /**
   * Close the CacheClientProxy of the client on the server and verify that
   * ha-stats are closed and the entry for the region is removed from
   * dispatchedMessagesMap.
   * 
   */
  public static void closeCacheClientProxyAndVerifyStats()
  {
    assertEquals("More than one BridgeServers found ", 1, cache
        .getBridgeServers().size());
    BridgeServerImpl bs = (BridgeServerImpl)cache.getBridgeServers().iterator()
        .next();
    assertNotNull(bs);
    assertNotNull(bs.getAcceptor());
    assertNotNull(bs.getAcceptor().getCacheClientNotifier());
    Iterator proxies = bs.getAcceptor().getCacheClientNotifier()
        .getClientProxies().iterator();
    assertTrue("No proxy found",proxies.hasNext());
    CacheClientProxy proxy = (CacheClientProxy)proxies.next();
    
    proxy.close();

    HARegionQueue rq = proxy.getHARegionQueue();
    Map dispatchedMsgMap = HARegionQueue.getDispatchedMessagesMapForTesting();

    assertTrue("HARegionQueue stats were not closed on proxy.close()", rq
        .getStatistics().isClosed());
    assertFalse(
        "HARegionQueue.dispatchedMessagesMap contains entry for the region even after proxy.close()",
        dispatchedMsgMap.containsKey(rq.getRegion().getName()));
  }

  /**
   * Do some PUT operations
   * 
   * @throws Exception -
   *                 thrown if any exception occurs in doing PUTs
   */
  public static void doEntryOperations() throws Exception
  {
    Region r1 = cache.getRegion(Region.SEPARATOR + REGION_NAME);
    String keyPrefix = "server-";
    for (int i = 0; i < 10; i++) {
      r1.put(keyPrefix + i, keyPrefix + "val-" + i);
    }
  }

  /**
   * Close the cache
   * 
   */
  public static void closeCache()
  {
    if (cache != null && !cache.isClosed()) {
      cache.close();
      cache.getDistributedSystem().disconnect();
    }
  }

}
