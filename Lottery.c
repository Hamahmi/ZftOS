  #include <stdio.h>
  #include <string.h>
  #include <stdlib.h>
  #include <stdbool.h>
  #include <time.h>

  struct Process {
    int ProcessID;
    int ArrivalTime;
    int CPUBurstTime;
    int StartTime;
    int EndTime;
    int NumberofTickets;
    int TotalTimeProcessed;
    int Turnaround;
    int WaitingTime;
  };

  #define MAX 100000 // Maximum number of processes in queue

//  struct Process intArray[MAX];
  int front = 0;
  int rear = -1;
  int itemCount = 0;

  struct Process peek() {
    return intArray[front];
  }

  bool isEmpty() {
    return itemCount == 0;
  }

  bool isFull() {
    return itemCount == MAX;
  }

  int size() {
    return itemCount;
  }

  void insert(struct Process data) {

    if(!isFull()) {

      if(rear == MAX-1) {
        rear = -1;
      }

      intArray[++rear] = data;
      itemCount++;
    }
  }

  struct Process removeData() {
    struct Process data = intArray[front++];

    if(front == MAX) {
        front = 0;
      }

      itemCount--;
      return data;
  }

  void printProcess( struct Process proc ) {

    printf( "Process ID : %d\n", proc.ProcessID);
    printf( "Arrival Time : %d\n", proc.ArrivalTime);
    printf( "CPU Burst Time : %d\n", proc.CPUBurstTime);
    printf( "Start Time : %d\n", proc.StartTime);
    printf( "End Time : %d\n", proc.EndTime);
    printf( "Number of Tickets : %d\n", proc.NumberofTickets);

  }

  long timer = 0;
  long totalTime = 0;

  void wait ( int msec ) {

    clock_t end_wait;
    end_wait = clock () + (msec/1000.0) * CLOCKS_PER_SEC;
    timer += msec;
    totalTime += msec;

    while (clock() < end_wait) {}

  }

  void resetTimer(){
    timer = 0;
  }

  long quantumLength;
  long totalNumberOfTickets;
  

  int final;

  struct Process arrivedLate[MAX];


int main()
{
    FILE *fp = NULL;
    int processCount = 0;  
    char inputfile[100];
    char ch; 
    // getting number of processes 
    while(fp ==NULL)
    {
    printf("Enter the name of the input file : ");
    scanf("%s", inputfile);
 
    fp = fopen(inputfile, "r");
 
    if (fp == NULL)
    {
        printf("Could not open file %s \n\n", inputfile);
    }
    }
    for (ch = getc(fp); ch != EOF; ch = getc(fp))
        if (ch == '\n')
            processCount = processCount + 1;
    fclose(fp);
    processCount = processCount-1;
    printf("The input file %s has %d processes(s)\n ", inputfile, processCount);

    struct Process[processCount];

    // This section is responsible for reading the inputs from the file, creating the processes and inserting them into the queue

    fp = fopen(inputfile, "r");
    char buff[255];
    fgets(buff, 255, (FILE*)fp);
    char *ptr;
    quantumLength = strtol(buff, &ptr, 10);
    fgets(buff, 255, (FILE*)fp); // To discard the total tickets input since it is not used in RR
    totalNumberOfTickets = strtol(buff, &ptr, 10);
    struct Process proc;
    int count = 0;
    int count1 = 0;

    while(fgets(buff, 255, (FILE*)fp)){


      char *found;
      char *str;
      str = strdup(buff);
      char a[4][255];
      int i = 0;
      while((found = strsep(&str, ",")) != NULL){
        strcpy(a[i], found);
        i++;
      }
      proc.ProcessID = strtol(a[0], &ptr, 10);
      proc.ArrivalTime = strtol(a[1], &ptr, 10);
      proc.CPUBurstTime = strtol(a[2], &ptr, 10);
      proc.NumberofTickets = strtol(a[3], &ptr, 10);
      proc.StartTime = -1;
      proc.EndTime = -1;
      proc.TotalTimeProcessed = 0;
      proc.Turnaround = 0;
      proc.WaitingTime = 0;
      if(proc.ArrivalTime == 0){
        insert(proc);
      }else{
        arrivedLate[count] = proc; // Bonus
        count++;
      }
      count1++;
    }
    fclose(fp);



 
    return 0;
}


/*
  int main(int argc, char *argv[]){

  // This section is responsible for reading the inputs from the file, creating the processes and inserting them into the queue
    FILE *fp;
    fp = fopen(argv[1], "r");
    char buff[255];
    fgets(buff, 255, (FILE*)fp);
    char *ptr;
    quantumLength = strtol(buff, &ptr, 10);
    fgets(buff, 255, (FILE*)fp); // To discard the total tickets input since it is not used in RR
    struct Process proc;
    int count = 0;
    int count1 = 0;

    while(fgets(buff, 255, (FILE*)fp)){


      char *found;
      char *str;
      str = strdup(buff);
      char a[4][255];
      int i = 0;
      while((found = strsep(&str, ",")) != NULL){
        strcpy(a[i], found);
        i++;
      }
      proc.ProcessID = strtol(a[0], &ptr, 10);
      proc.ArrivalTime = strtol(a[1], &ptr, 10);
      proc.CPUBurstTime = strtol(a[2], &ptr, 10);
      proc.NumberofTickets = strtol(a[3], &ptr, 10);
      proc.StartTime = -1;
      proc.EndTime = -1;
      proc.TotalTimeProcessed = 0;
      proc.Turnaround = 0;
      proc.WaitingTime = 0;
      if(proc.ArrivalTime == 0){
        insert(proc);
      }else{
        arrivedLate[count] = proc; // Bonus
        count++;
      }
      count1++;
    }
    fclose(fp);
    fp = fopen("LOT-Output.txt","w");

    // This section is concerned with the actual simulation of the file
    final = size() + count;
    struct Process finished[final];
    int j = 0;
    struct Process curProc;
    while(!isEmpty()){

      curProc = removeData();
      if(curProc.StartTime == -1){
        curProc.StartTime = totalTime;
      }
      fprintf(fp, "Time %ld: P%d Entering quantum\n",totalTime, curProc.ProcessID);
      printf("Time %ld: P%d Entering quantum\n",totalTime, curProc.ProcessID);
      while(1){
        wait(1);
        curProc.TotalTimeProcessed += 1;
        int c;
        for(c = 0; c < count; c++){ // Bonus
          if(arrivedLate[c].ArrivalTime == totalTime){
            insert(arrivedLate[c]);
          }
        }
        if(curProc.TotalTimeProcessed == curProc.CPUBurstTime){
          curProc.EndTime = totalTime;
          curProc.Turnaround = curProc.EndTime - curProc.ArrivalTime;
          curProc.WaitingTime = curProc.Turnaround - curProc.CPUBurstTime;
          finished[j] = curProc;
          j++;
          fprintf(fp, "Time %ld: P%d Done Turn around: %d Waiting time: %d\n",totalTime, curProc.ProcessID, curProc.Turnaround, curProc.WaitingTime);
          printf("Time %ld: P%d Done Turn around: %d Waiting time: %d\n",totalTime, curProc.ProcessID, curProc.Turnaround, curProc.WaitingTime);
          resetTimer();
          break;
        }
        if(timer == quantumLength){
          resetTimer();
          insert(curProc);
          break;
        }
      }

    }
    long totalWaiting = 0;
    long totalTurnaround = 0;
    int k;
    for (k = 0; k < final; k++) {
      totalWaiting += finished[k].WaitingTime;
      totalTurnaround += finished[k].Turnaround;
    }
    double avgWaiting = ((float)totalWaiting)/(k);
    double avgTurnaround = ((float)totalTurnaround)/(k);
    fprintf(fp, "\n");
    printf("\n");
    fprintf(fp, "Average Waiting Time = %f\n", avgWaiting);
    printf("Average Waiting Time = %f\n", avgWaiting);
    fprintf(fp, "Average Turnaround Time = %f", avgTurnaround);
    printf("Average Turnaround Time = %f", avgTurnaround);
    fprintf(fp, "\n");
    printf("\n");

    fclose(fp);
  }
*/