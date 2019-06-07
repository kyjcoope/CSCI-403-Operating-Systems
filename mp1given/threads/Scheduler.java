// Scheduler.java
//	Class to choose the next thread to run.
//
// 	These routines assume that interrupts are already disabled.
//	If interrupts are disabled, we can assume mutual exclusion
//	(since we are on a uniprocessor).
//
// 	NOTE: We can't use Locks to provide mutual exclusion here, since
// 	if we needed to wait for a lock, and the lock was busy, we would 
//	end up calling FindNextToRun(), and that would put us in an 
//	infinite loop.
//
// 	Very simple implementation -- no priorities, straight FIFO.
//	Might need to be improved in later assignments.
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.


//------------------------------------------------------------------------
// Create a handler for scheduled interrupts for Round Robin 
// implementation.
// 
// MP1
//------------------------------------------------------------------------
//class YourHandler 


class Scheduler {

  static private List readyList;// queue of threads that are ready to run,
				// but not running
  static private SchedHandler RR_interrupt_handler;
  //constants for scheduling policies
  static final int POLICY_PRIO_NP = 1;
  static final int POLICY_PRIO_P = 2;
  static final int POLICY_RR = 3;
  static final int POLICY_SJF_NP = 4;
  static final int POLICY_SJF_P = 5;
  static final int POLICY_FCFS = 6;

  static int policy=POLICY_FCFS;
  static public NachosThread threadToBeDestroyed;
  static{
  	RR_interrupt_handler = new SchedHandler();
  }
  //----------------------------------------------------------------------
  // Scheduler
  // 	Initialize the list of ready but not running threads to empty.
  //----------------------------------------------------------------------

  static { 
    readyList = new List(); 
  } 

  //----------------------------------------------------------------------
  // start
  // 	called by a Java thread (usually the initial thread that calls 
  //    Nachos.main). Starts the first Nachos thread.
  //
  //----------------------------------------------------------------------

  public static void start() {
    NachosThread nextThread;

    Debug.println('t', "Scheduling first Nachos thread");
    
    nextThread = findNextToRun();
    if (nextThread == null) {
      Debug.print('+', "Scheduler.start(): no NachosThread ready!");
      return;
    }

    Debug.println('t', "Switching to thread: " + nextThread.getName());
   

    synchronized (nextThread) {
      nextThread.setStatus(NachosThread.RUNNING);
      nextThread.notify();
    }
    


    // nextThread is now running
    
  }

  //----------------------------------------------------------------------
  // readyToRun
  // 	Mark a thread as ready, but not running.
  //	Put it on the ready list, for later scheduling onto the CPU.
  //
  //	"thread" is the thread to be put on the ready list.
  //----------------------------------------------------------------------

  public static void readyToRun(NachosThread thread) {
    Debug.print('t', "Putting thread on ready list: " + thread.getName() + 
		"\n");

    thread.setStatus(NachosThread.READY);
    NachosThread temp;
    //MP1 
    switch (policy) //policy switch
    {
       case POLICY_FCFS: 
	  readyList.append(thread); //FCFS, put at front
	  break;

       case POLICY_RR:
          readyList.append(thread); //RR is FCFS so, put at front
          break;

       case POLICY_SJF_NP:
          readyList.sortedInsert(thread,thread.getTimeLeft()); //key is CPU burst-time, do sorted insert
          break;

       case POLICY_SJF_P:
          readyList.sortedInsert(thread,thread.getTimeLeft()); //key is CPU burst-time, do sorted insert
	  break;

       case POLICY_PRIO_NP:
          readyList.sortedInsert(thread,thread.getThreadPriority()); //key is thread Priority, do sorted insert
          break;
          
       case POLICY_PRIO_P:
          readyList.sortedInsert(thread,thread.getThreadPriority()); //key is thread Priority, do sorted insert
	  break;

       default:
	  readyList.append(thread); //default
	  break;
    }
  }
  
  //----------------------------------------------------------------------
  // findNextToRun
  // 	Return the next thread to be scheduled onto the CPU.
  //	If there are no ready threads, return null.
  // Side effect:
  //	Thread is removed from the ready list.
  //----------------------------------------------------------------------

  public static NachosThread findNextToRun() {
    return (NachosThread)readyList.remove();
  }

  //----------------------------------------------------------------------
  // run
  // 	Dispatch the CPU to nextThread.  Save the state of the old thread,
  //	and load the state of the new thread, by calling the machine
  //	dependent context switch routine, SWITCH.
  //
  //      Note: we assume the state of the previously running thread has
  //	already been changed from running to blocked or ready (depending).
  // Side effect:
  //	The global variable currentThread becomes nextThread.
  //
  //	"nextThread" is the thread to be put into the CPU.
  //----------------------------------------------------------------------

  public static void run(NachosThread nextThread) {
    NachosThread oldThread;

    oldThread = NachosThread.thisThread();
    
    if (Nachos.USER_PROGRAM) {  // ignore until running user programs 
      if (oldThread.space != null) {// if this thread runs a user program,
        oldThread.saveUserState(); // save the user's CPU registers
	oldThread.space.saveState();
      }
    }


    //MP1 Round Robin - schedule an interrupt if necessary
    if(policy==POLICY_RR){
        Interrupt.schedule( RR_interrupt_handler, 40, Interrupt.TimerInt );
    }  

    Debug.println('t', "Switching from thread: " + oldThread.getName() +
		  " to thread: " + nextThread.getName());

    // We do this in Java via wait/notify of the underlying Java threads.

    synchronized (nextThread) {
      nextThread.setStatus(NachosThread.RUNNING);
      nextThread.notify();
    }
    synchronized (oldThread) {
      while (oldThread.getStatus() != NachosThread.RUNNING) 
	try {oldThread.wait();} catch (InterruptedException e) {};
    }
    
    Debug.println('t', "Now in thread: " + NachosThread.thisThread().getName());

    // If the old thread gave up the processor because it was finishing,
    // we need to delete its carcass. 
    if (threadToBeDestroyed != null) {
      threadToBeDestroyed.stop();
      threadToBeDestroyed = null;
    }
    
    if (Nachos.USER_PROGRAM) {
      if (oldThread.space != null) {// if there is an address space
	oldThread.restoreUserState();     // to restore, do it.
	oldThread.space.restoreState();
      }
    }
  }

  //----------------------------------------------------------------------
  // print
  // 	Print the scheduler state -- in other words, the contents of
  //	the ready list.  For debugging.
  //----------------------------------------------------------------------
  public static void print() {
    System.out.print("Ready list contents:");
    readyList.print();
  }

  public static void setSchedulerPolicy(int p)
  {
     policy = p;
  }

  //----------------------------------------------------------------------
  // shouldISwitch
  //    Checks to see if current thread should be preempted	
  //----------------------------------------------------------------------

  public static boolean shouldISwitch(NachosThread current, NachosThread newThread)
  {
     //MP1 preemption code
     switch(policy){ //policy switch
       case POLICY_PRIO_P:  //preemption PRIO
         if(newThread.getThreadPriority()<current.getThreadPriority()){ //change threads if new one is of lower priority
           return true;
         }
       case POLICY_SJF_P: //preemption SJF
         if(newThread.getTimeLeft()<current.getTimeLeft()){ //change threads if new one is has lower CPU burst time
           return true;
         }
       default:
         return false;
     }
     //return false;  //default
  }
}


