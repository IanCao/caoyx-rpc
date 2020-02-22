package com.caoyx.rpc.benchmark.thrift;

import com.caoyx.rpc.benchmark.thrift.impl.UserService;
import com.caoyx.rpc.benchmark.thrift.impl.UserService.Iface;
import com.caoyx.rpc.benchmark.thrift.server.UserServiceThriftServerImpl;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;

import java.net.InetSocketAddress;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-19 23:41
 */
public class Server {
    public static void main(String[] args) throws TTransportException {
        InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 8080);

        TNonblockingServerTransport serverSocket = new TNonblockingServerSocket(serverAddress);
        TThreadedSelectorServer.Args serverParams = new TThreadedSelectorServer.Args(serverSocket);
        serverParams.protocolFactory(new TBinaryProtocol.Factory());
        serverParams.processor(new UserService.Processor<Iface>(new UserServiceThriftServerImpl()));
        TServer server = new TThreadedSelectorServer(serverParams);
        server.serve();
    }

}