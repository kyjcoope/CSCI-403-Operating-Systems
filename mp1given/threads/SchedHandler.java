class SchedHandler implements Runnable{
    public void run(){	
	NachosThread currentThread;
	currentThread = NachosThread.thisThread();
	currentThread.Yield();
    }

}
