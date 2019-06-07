//class BoundedBuffer
//This class implements the synchronization methods to be used in 
//the bounded buffer problem 

public class BoundedBuffer
{
   //MP2 create any variables you need

   //BoundedBuffer
   //constructor:  initialize any variables that are needed for a bounded 
   //buffer of size "size"
   public BoundedBuffer(int size)
   {
   }

   //produce()
   //produces a character c.  If the buffer is full, wait for an empty
   //slot
   public void produce(char c)
   {
     //MP2
     
   }

   //consume()
   //consumes a character.  If the buffer is empty, wait for a producer.
   //use method SynchTest.addToOutputString(c) upon consuming a character. 
   //This is used to test your implementation.
   public void consume()
   {
     //MP2
     //make sure you change the following line accordingly
     SynchTest.addToOutputString('c');

   }

}
