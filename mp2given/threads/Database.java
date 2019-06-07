//class Database
//This class implements the synchronization methods to be used in 
//the readers writers problem
public class Database
{
   //MP2 create any variables that you need for implementation of the methods
   //of this class

   //Database
   //Initializes Database variables
   public Database()
   {
     //MP2
   }

   //napping()
   //this is called when a reader or writer wants to go to sleep and when 
   //a reader or writer is doing its work.
   //Do not change for MP2
   public static void napping()
   {
      Alarm ac = new Alarm(20);  
   }

   //startRead
   //this function should block any reader that wants to read if there 
   //is a writer that is currently writing.
   //it returns the number of readers currently reading including the
   //new reader.
   public int startRead()
   {
      //MP2
      return 0;
   }

   //endRead()
   //This function is called by a reader that has finished reading from the 
   //database.  It returns the current number of readers excluding the one who
   //just finished.
   public int endRead()
   {
      //MP2
      return 0;
   }

   //startWrite()
   //This function should allow only one writer at a time into the Database
   //and block the writer if anyone else is accessing the database for read 
   //or write.
   public void startWrite()
   {
      //MP2

   }
   
   //endWrite()
   //signal that a writer is done writing and the database can now be accessed
   //by someone who is waiting to read or write.
   public void endWrite()
   {
      //MP2
   }
}
