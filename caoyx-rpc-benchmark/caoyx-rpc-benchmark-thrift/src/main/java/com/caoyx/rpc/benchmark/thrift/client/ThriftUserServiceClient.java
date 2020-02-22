package com.caoyx.rpc.benchmark.thrift.client;

import com.caoyx.rpc.benchmark.thrift.impl.UserService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.Closeable;
import java.io.IOException;

public class ThriftUserServiceClient implements Closeable {

	// not thread safe
	public final TTransport transport;
	public final TProtocol protocol;
	public final UserService.Client client;

	public ThriftUserServiceClient(String host, int port) {
		transport = new TFramedTransport(new TSocket(host, port));
		protocol = new TBinaryProtocol(transport);
		client = new UserService.Client(protocol);

		try {
			transport.open();
		} catch (TTransportException e) {
			throw new Error(e);
		}
	}

	@Override
	public void close() throws IOException {
		transport.close();
	}

}
