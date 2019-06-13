return {
  no_consumer = true,
  fields = {
    halt ={
      type = "table",
      schema = {
       fields ={
           msg ={type = "string", required = true},
           delay ={type = "boolean", default = true},
        }
      }
     }
  }
}
