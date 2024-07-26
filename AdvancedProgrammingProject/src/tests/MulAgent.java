package tests;

import configs.BinOpAgent;

public class MulAgent extends BinOpAgent {

    public MulAgent(String[] subs, String[] pubs) {
        super("MulAgent", subs[0], subs[1], pubs[0], (a, b) -> a * b);
    }
}
