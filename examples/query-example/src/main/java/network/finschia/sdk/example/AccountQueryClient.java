package network.finschia.sdk.example;

import cosmos.auth.v1beta1.QueryGrpc;
import cosmos.auth.v1beta1.QueryOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

public class AccountQueryClient implements Closeable {
    private ManagedChannel channel;
    private QueryGrpc.QueryBlockingStub stub;

    public AccountQueryClient(ManagedChannel channel){
        this.channel = channel;
        this.stub = QueryGrpc.newBlockingStub(channel);
    }

    @Override
    public void close() {
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public QueryOuterClass.QueryAccountResponse queryAccount(String address){
        final QueryOuterClass.QueryAccountRequest accountRequest = QueryOuterClass.QueryAccountRequest.newBuilder().setAddress(address).build();
        return this.stub.account(accountRequest);
    }

    public QueryOuterClass.QueryAccountsResponse queryAccounts(){
        final QueryOuterClass.QueryAccountsRequest accountsRequest = QueryOuterClass.QueryAccountsRequest.newBuilder().build();
        return this.stub.accounts(accountsRequest);
    }

    public QueryOuterClass.QueryParamsResponse queryParams(){
        final QueryOuterClass.QueryParamsRequest paramsRequest = QueryOuterClass.QueryParamsRequest.newBuilder().build();
        return this.stub.params(paramsRequest);
    }

    public static void main(String[] args) {
        final int port = 9090;
        final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build();
        final AccountQueryClient client = new AccountQueryClient(channel);

        final QueryOuterClass.QueryAccountResponse accountResponse = client.queryAccount("link146asaycmtydq45kxc8evntqfgepagygelel00h");
        System.out.println(accountResponse);

        final QueryOuterClass.QueryAccountsResponse accountsResponse = client.queryAccounts();
        System.out.println(accountsResponse);

        final QueryOuterClass.QueryParamsResponse paramsResponse = client.queryParams();
        System.out.println(paramsResponse);
    }
}
