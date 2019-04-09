package com.sovrn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Environment;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.context.annotation.Bean;

public class ZKManager {

  private static ZooKeeper zkeeper;
  private static ZKConnection zkConnection;
  private static final Logger LOG = LoggerFactory.getLogger(ZKManager.class);
  private final String host;

  public ZKManager(String host) {
    this.host = host;
    initialize();
  }

  @Bean
  void initialize() {
    zkConnection = new ZKConnection();
    try {
      zkeeper = zkConnection.connect(this.host);
    } catch (IOException | InterruptedException e) {
      LOG.error(e.getMessage());
    }
  }

  public void createOrUpdate(String path, byte[] data) throws
    KeeperException, InterruptedException{

    Stat stat = zkeeper.exists(path, true);
    if (stat != null) {
      update(path, data);
    } else {
      create(path, data);
    }
  }

  public void closeConnection() {
    try {
      zkConnection.close();
    } catch (InterruptedException e) {
      LOG.error(e.getMessage());
    }
  }

  public void create(String path, byte[] data)
    throws KeeperException,
    InterruptedException {

    zkeeper.create(
      path,
      data,
      ZooDefs.Ids.OPEN_ACL_UNSAFE,
      CreateMode.PERSISTENT);
  }

  public Object getZNodeData(String path, boolean watchFlag)
    throws KeeperException,
    InterruptedException,
    UnsupportedEncodingException {

    byte[] b = null;
    b = zkeeper.getData(path, null, null);
    return new String(b, "UTF-8");
  }

  public void update(String path, byte[] data) throws KeeperException,
    InterruptedException {
    int version = zkeeper.exists(path, true).getVersion();
    zkeeper.setData(path, data, version);
  }

}
