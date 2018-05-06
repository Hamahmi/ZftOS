#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <stdlib.h>

struct Page {
  int PageID;
  bool Referenced;
  bool Modified;
};

#define MAXP 5

struct Page array[MAXP];
int pageCount = 0;

void printPage (struct Page page){
  if(page.PageID < 10)
    printf("PageID: %d , Referenced: %d, Modified: %d\n", page.PageID, page.Referenced, page.Modified);
  else
    printf("PageID: %d, Referenced: %d, Modified: %d\n", page.PageID, page.Referenced, page.Modified);
}

bool isEmptyP() {
  return pageCount == 0;
}

bool isFullP() {
  return pageCount == MAXP;
}

void insertP (struct Page page){
  array[pageCount] = page;
  pageCount++;
}

struct Page removeP (int index){
  int i;
  struct Page removed;
  removed = array[index];
  for (i = index; i < pageCount-1; i++){
    array[i] = array[i+1];
  }
  pageCount--;
  return removed;
}

int checkExistP (int pageID){
  int i;
  for (i = 0; i < pageCount; i++){
    if(array[i].PageID == pageID){
      return i;
    }
  }
  return -1;
}

struct Request {
  int AccessTime;
  int PageID;
  char Type;
};

#define MAX 100000 // Maximum number of requests in queue

struct Request intArray[MAX];
int front = 0;
int rear = -1;
int itemCount = 0;

struct Request peek() {
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

void insert(struct Request data) {
  if(!isFull()) {
    if(rear == MAX-1) {
      rear = -1;
    }
    intArray[++rear] = data;
    itemCount++;
  }
}

struct Request removeData() {
  struct Request data = intArray[front++];
  if(front == MAX) {
      front = 0;
    }
    itemCount--;
    return data;
}

int main(int argc, char *argv[]){
  FILE *fp;
  fp = fopen(argv[1], "r");
  char buff[255];
  char *ptr;
  struct Request req;
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
    req.AccessTime = strtol(a[0], &ptr, 10);
    req.PageID     = strtol(a[1], &ptr, 10);
    req.Type       = *a[2];
    insert(req);
  }
  fclose(fp);

  //--------------------------------------------------------//
  int time = 0;

  while(!isEmpty()){

      if(peek().AccessTime == time){
        struct Request currentReq = removeData();
        int k = checkExistP(currentReq.PageID);
        if(k == -1){
          // The page does NOT exist (Page fault)
          struct Page inserted;
          inserted.PageID = currentReq.PageID;
          inserted.Referenced = true;
          if(currentReq.Type == 'W'){
            // set modified bit if access is to write
            inserted.Modified = true;
          }else{
            inserted.Modified = false;
          }
          if(isFullP()){
            while(array[0].Referenced){
              // If R is 1, clear it, put the page at the back until the 1st one's R is 0
              struct Page removed;
              removed = removeP(0);
              removed.Referenced = false;
              insertP(removed);
            }
            int i;
            bool flag = false;
            for (i = 0; i < pageCount; i++){
              // Evict the 1st none-modified page
              if(!array[i].Referenced && !array[i].Modified){
                struct Page removed;
                removed = removeP(i);
                flag = true;
                // The page evicted was NOT modified, just evict it
                printf("Page fault on time %d, evicted page: %d, loaded page: %d.\n", time, removed.PageID, inserted.PageID);
                break;
              }
            }
            if(!flag){
              // If no pages were evicted (all were modified), evict the 1st page
              struct Page removed;
              removed = removeP(0);
              // The page evicted was modified, need to be written back to disk
              printf("Page fault on time %d, evicted page: %d (written back to disk), loaded page: %d.\n", time, removed.PageID, inserted.PageID);
            }
          }else{
            // Memory not full , just insert the page
            printf("Page fault on time %d, evicted page: none, loaded page: %d.\n", time, inserted.PageID);
          }
          insertP(inserted);
        }else{
          // The page exists
          printf("Page %d already in memory.\n", currentReq.PageID);
          if(currentReq.Type == 'W'){
            // set modified bit if access is to write
            array[k].Modified = true;
          }
          // set referenced bit on access
          array[k].Referenced = true;
        }
      }
      printf("In memory at time %d: \n",time);
      int j;
      for (j = 0; j < pageCount; j++){
          printPage(array[j]);
      }
      printf("\n");
      time++;
      if(time%20 == 0){
        printf("Clock interrupt at time %d\n",time);
        printf("\n");
        int i;
        for (i = 0; i < pageCount; i++){
          array[i].Referenced = false;
        }
      }
  }
}
