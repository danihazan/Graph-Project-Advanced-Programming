package project_biu.configs;

public interface Config {
    void create() throws Exception;
    String getName();
    int getVersion();
    void close();
}
