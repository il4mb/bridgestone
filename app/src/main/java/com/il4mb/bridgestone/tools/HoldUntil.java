package com.il4mb.bridgestone.tools;

public class HoldUntil {

    private static Executor executor;
    private static int millisecond;
    public HoldUntil(Executor executor, int millisecond) {
        HoldUntil.millisecond = millisecond;
        HoldUntil.executor    = executor;
    }

    public static void main(String args[]) {
        try
        {
            Thread.sleep(HoldUntil.millisecond);
            HoldUntil.executor.onDone();

            System.out.println("executed");

        }
        catch(InterruptedException ex)
        {
            ex.printStackTrace();

        } finally {

            HoldUntil.executor    = null;
            HoldUntil.millisecond = 0;
        }
    }

    public static interface Executor {
        void onDone();
    }
}
