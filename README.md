# ImagesSubRedditViewer
/app
Basic UI to show images from /r/images in a recyclerview
Using MVP architecture to get images url asynchronously


/imageLoader
Check using the url hash if the image bitmap is present in memorycache if false else load into imageview
check if the image bitmap is present in diskcache asynchronously  if false else load into imageview
get the image from given url asynchronously store in disk and memory in bitmap format 
Repeat the process on next call as image is already stored either on memory or disk will load faster.
