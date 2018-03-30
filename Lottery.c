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
int totalTickets;
struct Process array[MAX];

int main(int argc, char *argv[]){

// This section is responsible for reading the inputs from the file, creating the processes and inserting them into the array
  FILE *fp;
  fp = fopen(argv[1], "r");
  char buff[255];
  fgets(buff, 255, (FILE*)fp);
  char *ptr;
  quantumLength = strtol(buff, &ptr, 10);
  fgets(buff, 255, (FILE*)fp);
  totalTickets = strtol(buff, &ptr, 10);
  struct Process proc;

  int procCount = 0;
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
    array[procCount] = proc;
    procCount++;
  }
  fclose(fp);

  // Create tickets array
  int tickets[totalTickets];
  int i;
  int c;
  int tc = 0;
  for(i = 0;i<procCount;i++){
    int tick = array[i].NumberofTickets;
    for(c = 0;c<tick;c++){
      tickets[tc] = (i+1);
      tc++;
    }
  }

  fp = fopen("Lottery-Output.txt","w");

  //This section is concerned with the actual simulation of the file
  srand(time(NULL));
  struct Process finished[procCount];
  int j = 0;
  while(j!=procCount){

    int random_number = rand() % (totalTickets-1);
    int procNumber = tickets[random_number];
    if(array[procNumber-1].TotalTimeProcessed != array[procNumber-1].CPUBurstTime){
      if(array[procNumber-1].StartTime == -1){
        array[procNumber-1].StartTime = totalTime;
      }
      fprintf(fp, "Time %ld: P%d Entering quantum\n",totalTime, array[procNumber-1].ProcessID);
      printf("Time %ld: P%d Entering quantum\n",totalTime, array[procNumber-1].ProcessID);
      while(1){
        wait(1);
        array[procNumber-1].TotalTimeProcessed += 1;
        if(array[procNumber-1].TotalTimeProcessed == array[procNumber-1].CPUBurstTime){
          array[procNumber-1].EndTime = totalTime;
          array[procNumber-1].Turnaround = array[procNumber-1].EndTime - array[procNumber-1].ArrivalTime;
          array[procNumber-1].WaitingTime = array[procNumber-1].Turnaround - array[procNumber-1].CPUBurstTime;
          finished[j] = array[procNumber-1];
          j++;
          fprintf(fp, "Time %ld: P%d Done Turn around: %d Waiting time: %d\n",totalTime, array[procNumber-1].ProcessID, array[procNumber-1].Turnaround, array[procNumber-1].WaitingTime);
          printf("Time %ld: P%d Done Turn around: %d Waiting time: %d\n",totalTime, array[procNumber-1].ProcessID, array[procNumber-1].Turnaround, array[procNumber-1].WaitingTime);
          resetTimer();
          break;
        }
        if(timer == quantumLength){
          resetTimer();
          break;
        }
      }
    }

  }
  long totalWaiting = 0;
  long totalTurnaround = 0;
  int k;
  for (k = 0; k < j; k++) {
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
