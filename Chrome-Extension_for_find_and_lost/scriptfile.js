

var xmlhttp = new XMLHttpRequest();
var url = "http://192.168.1.101:3000/";

xmlhttp.open("GET", url+"notes", true);
xmlhttp.send();
xmlhttp.onreadystatechange = function (e) {
   if (xmlhttp.readyState == 4 && xmlhttp.status==200){
       console.log("done");
       var obj=JSON.parse(this.responseText);
       var x="";
       for(i in obj)
           {
               if(obj[i].imgpath)
                   {
                     //  x+="<img scr="+(url+"images/"+obj[i].imgpath).url()+">";
                   x=x+"<br>"+ "<img src="+url+"images/"+obj[i].imgpath+" " +" alt=Flowers in Chania width=360 height=245>";
                       console.log(url+"images/"+obj[i].imgpath+"\n");
                   }
               else
                   {
                       x+="<br>"+ obj[i].message;
                   }
           }
       document.getElementById("demo").innerHTML =x;
       
   }
    else{
        document.getElementById("demo").innerHTML = this.readyState+" "+this.status+ this.responseText+"there";
        console.log("message");
    }
};


