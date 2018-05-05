#include <stdio.h>
#include <stdbool.h>

struct Page {
  int PageID;
  bool Referenced;
  bool Modified;
};

#define MAX 5

struct Page array[MAX];
int pageCount = 0;

bool isEmpty() {
  return pageCount == 0;
}

bool isFull() {
  return pageCount == MAX;
}

void insert (struct Page page){
  array[pageCount] = page;
  pageCount++;
}

struct Page remove (int index){
  int i;
  for (i = index; i < MAX-1; i++){
    array[i] = array[i+1];
  }
  array[MAX-1] = NULL;
  pageCount--;
}

bool checkExist (int pageID){
  int i;
  for (i = 0; i < pageCount){
    if(array[i].PageID == pageID){
      return true;
    }
  }
  return false;
}

int main(){

}
