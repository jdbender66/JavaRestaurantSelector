package restaurantSelector;

//imports
import java.awt.*;
import javax.swing.*;
import smile.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
* Joe Bender, JoAnn Dwornicki, Tiancheng Dong
*/

public class RestuarantSelector {
    //variable declarations
	public Object placeobject;
	public String placeswitch;
	
	public JScrollPane scrollPane;
	
	public ArrayList<restaurantEntry> returnedresults = new ArrayList<restaurantEntry>();
	
	public Object categoryobject;
	public String categoryswitch;
	
	public Object priceobject;
	public String priceswitch;
	
	public Object ratingobject;
	public String ratingswitch;
	
	public Object liobject;
	public String liswitch;
	
	public Object miobject;
	public String miswitch;
	
	public String url = "jdbc:mysql://localhost:3306/restaurants";
	public String username = "root";
	public String password = "";
	
	public double[] prob;
	public double[] prob2;
	public double[] prob3;
	public double[] prob4;
	private JFrame mainFrame;
	private JLabel title;
	private JLabel place;
	private JLabel type;
	private JLabel ppoint;
	private JLabel rate;
	private JLabel pref;
	private JLabel mi;
	private JLabel li;
	private JLabel lister;
    private JPanel mainPanel;
	private JPanel restaurantPanel;
	private JTextArea rankings; 
	    
    //call gui builder
	public RestuarantSelector(){
		buildingGUI();
	}
    //main method, create instance of MainGUI
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		RestuarantSelector restaurantSelectorGUI = new RestuarantSelector();
	}	
	
	//comparator class, sorts final array based on model-calculated rank
	class RestaurantComparator implements Comparator<restaurantEntry>{
		  public int compare(restaurantEntry rest1, restaurantEntry rest2){
			  Double value1 = rest1.getValue();
			  Double value2 = rest2.getValue();
			  if(value1 > value2)
				  return -1;
			  else if(value1 < value2)
				  return 1;
			  else
				  return 0;
		  }
	  }
	
	//an object to hold information about each restaurant
	private static class restaurantEntry{
			//variables to hold info taken from database
		  private String restname;
		  private String address;
		  private String phone;
		  private String url;
		  private String food;
		  private String price;
		  private String rating;
		  private String neighborhood;
		  private double value;
		  
		  //constructor for object
		  public restaurantEntry(String name, String address, String phone, String url, String neighborhood, String food, String price, String rating, double value){
		    super();
			this.restname = name;
		    this.address = address;
		    this.phone = phone;
		    this.url = url;
		    this.food = food;
		    this.price = price;
		    this.rating = rating;
		    this.value = value;
		    this.neighborhood = neighborhood;
		  
		  }

		  public String getName() {
		    return this.restname;
		  }
		  
		  public String getAddress() {
			    return this.address;
			  }
		  
		  public String getPhone() {
			    return this.phone;
			  }
		  
		  public String getUrl() {
			    return this.url;
			  }
		  public String getFood() {
			    return this.food;
			  }
		  public String getPrice() {
			    return this.price;
			  }
		  public String getNeighborhood() {
			    return this.neighborhood;
			  }
		  public String getRating() {
			    return this.rating;
			  }

		  public double getValue() {
		    return this.value;
		  }
		}
	
	
		// Builds the GUI
		public void buildingGUI() {     
            //Build Frame and set layout
			mainFrame = new JFrame("Restaurant Selector"); // Sets title of the GUI
            mainFrame.setVisible(true);
			mainFrame.setSize(1000, 700); // Sets size of GUI
			mainFrame.setLayout(new BorderLayout()); 
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
                      
            //create main panel
            mainPanel = new JPanel(new GridBagLayout());
            //create banner panel
            restaurantPanel = new JPanel(new GridBagLayout());
                      
            //layout constraints
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(10,10,10,10);
            c.weightx = 1;
            c.fill=GridBagConstraints.HORIZONTAL;
            
            GridBagConstraints d = new GridBagConstraints();
            d.insets = new Insets(10,10,10,10);
            d.weightx = 1;
            d.fill=GridBagConstraints.HORIZONTAL;
                            
			//Declare titles for interface
            title = new JLabel("Pittsburgh Restaurant Selector!");
            title.setFont(new Font(title.getFont().getName(), title.getFont().getStyle(), 20));
            place = new JLabel("Neighborhood:");
            type = new JLabel("Type of Food:");
            ppoint = new JLabel("Price Point:");
            rate = new JLabel("Rating:");
            pref = new JLabel("User Preferences");
            pref.setFont(new Font(pref.getFont().getName(), pref.getFont().getStyle(), 15));
            mi = new JLabel("Most Important Field:");
            li = new JLabel("Least Important Field:");
            lister = new JLabel("Restaurant Results:");
            lister.setFont(new Font(lister.getFont().getName(), lister.getFont().getStyle(), 15));
            
            //Arrays to fill combo boxes
            String[] neighborhoods = {"Lawrenceville", "Shadyside", "South Side", "Downtown", "North Side", "Squirrel Hill", "Oakland", "Strip District" };
            String[] categories = {"American", "Chinese", "French", "Greek", "Italian", "Indian", "Mexican", "Thai"};
            String[] prices = {"$", "$$", "$$$", "$$$$"};
            String[] ratings = {"1", "1.5","2","2.5", "3","3.5", "4","4.5", "5"};
            String[] criteria = {"Neighborhood", "Category", "Price Point", "Rating"};
            
            //Declare combo boxes and fill with arrays
            final JComboBox<String> comboPlace  = new JComboBox<>(neighborhoods);
            final JComboBox<String> comboFood  = new JComboBox<>(categories);
            final JComboBox<String> comboPrice  = new JComboBox<>(prices);
            final JComboBox<String> comboRate  = new JComboBox<>(ratings);
            
            final JComboBox<String> micombo  = new JComboBox<>(criteria);
            final JComboBox<String> licombo  = new JComboBox<>(criteria);
            
            micombo.setSelectedItem("Rating");
            licombo.setSelectedItem("Neighborhood");
            
            //Create text area where results will be held, modify it's settings
            rankings = new JTextArea(20, 40);
            
            //make results scrollable
			scrollPane = new JScrollPane(rankings,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
            rankings.setEditable(false);
            rankings.setLineWrap(true);
            rankings.setWrapStyleWord(true);
            
            
            //Create submit button and attach action listener
            JButton submit = new JButton("Get Restaurants");
            submit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	//clearing the text area and restaurant arraylist to deal with multiple queries
                	rankings.setText(null);
                	returnedresults.clear();
                	try {
                		
                		//get value from place combobox
                		placeobject = comboPlace.getSelectedItem();
                        placeswitch = placeobject.toString();
                        
                        //get value from category combobox
                		categoryobject = comboFood.getSelectedItem();
                        categoryswitch = categoryobject.toString();
                        
                        //get value from price combobox
                		priceobject = comboPrice.getSelectedItem();
                        priceswitch = priceobject.toString();
                        
                        //get value from rating combobox
                		ratingobject = comboRate.getSelectedItem();
                        ratingswitch = ratingobject.toString();
                        
                        //get value from least important combobox
                		liobject = licombo.getSelectedItem();
                        liswitch = liobject.toString();
                        
                        //get value from most important combobox
                		miobject = micombo.getSelectedItem();
                        miswitch = miobject.toString();
                        
                        
                	    //accesses model
         			    Network net = new Network();
         			    net.readFile("model.xdsl");
         			    net.updateBeliefs();
         			    
         			    //if statement to take in user selected neighborhood, and pass it into the model utility node
         			    if(placeswitch.equals("Lawrenceville")){
         			    	double[] prob = {1,0,0,0,0,0,0,0};
         			    	net.setNodeDefinition("NeighborhoodU", prob);
              			    net.updateBeliefs();
         			    }else if(placeswitch.equals("Shadyside")){
         			    	double[] prob = {0,1,0,0,0,0,0,0};
         			    	net.setNodeDefinition("NeighborhoodU", prob);
              			    net.updateBeliefs();
         			    }else if(placeswitch.equals("South Side")){
         			    	double[] prob = {0,0,1,0,0,0,0,0};
         			    	net.setNodeDefinition("NeighborhoodU", prob);
              			   net.updateBeliefs();
         			    }else if(placeswitch.equals("Downtown")){
         			    	double[] prob = {0,0,0,1,0,0,0,0};
         			    	net.setNodeDefinition("NeighborhoodU", prob);
              			    net.updateBeliefs();
         			    }else if(placeswitch.equals("North Side")){
         			    	double[] prob = {0,0,0,0,1,0,0,0};
         			    	net.setNodeDefinition("NeighborhoodU", prob);
              			    net.updateBeliefs();
         			    }else if(placeswitch.equals("Squirrel Hill")){
         			    	double[] prob = {0,0,0,0,0,1,0,0};
         			    	net.setNodeDefinition("NeighborhoodU", prob);
              			    net.updateBeliefs();
         			    }else if(placeswitch.equals("Oakland")){
         			    	double[] prob = {0,0,0,0,0,0,1,0};
         			    	net.setNodeDefinition("NeighborhoodU", prob);
              			    net.updateBeliefs();
         			    }else if(placeswitch.equals("Strip District")){
         			    	double[] prob = {0,0,0,0,0,0,0,1};
         			    	net.setNodeDefinition("NeighborhoodU", prob);
              			    net.updateBeliefs();
         			    }
         			    
         			   //if statement to take in user selected category, and pass it into the model utility node
         			   if(categoryswitch.equals("American")){
        			    	double[] prob2 = {1,0,0,0,0,0,0,0};
        			    	net.setNodeDefinition("CategoryU", prob2);
             			    net.updateBeliefs();
        			    }else if(categoryswitch.equals("Chinese")){
        			    	double[] prob2 = {0,1,0,0,0,0,0,0};
        			    	net.setNodeDefinition("CategoryU", prob2);
             			    net.updateBeliefs();
        			    }else if(categoryswitch.equals("French")){
        			    	double[] prob2 = {0,0,1,0,0,0,0,0};
        			    	net.setNodeDefinition("CategoryU", prob2);
             			   net.updateBeliefs();
        			    }else if(categoryswitch.equals("Greek")){
        			    	double[] prob2 = {0,0,0,1,0,0,0,0};
        			    	net.setNodeDefinition("CategoryU", prob2);
             			    net.updateBeliefs();
        			    }else if(categoryswitch.equals("Italian")){
        			    	double[] prob2 = {0,0,0,0,1,0,0,0};
        			    	net.setNodeDefinition("CategoryU", prob2);
             			    net.updateBeliefs();
        			    }else if(categoryswitch.equals("Indian")){
        			    	double[] prob2 = {0,0,0,0,0,1,0,0};
        			    	net.setNodeDefinition("CategoryU", prob2);
             			    net.updateBeliefs();
        			    }else if(categoryswitch.equals("Mexican")){
        			    	double[] prob2 = {0,0,0,0,0,0,1,0};
        			    	net.setNodeDefinition("CategoryU", prob2);
             			    net.updateBeliefs();
        			    }else if(categoryswitch.equals("Thai")){
        			    	double[] prob2 = {0,0,0,0,0,0,0,1};
        			    	net.setNodeDefinition("CategoryU", prob2);
             			    net.updateBeliefs();
        			    }
         			   
         			   //if statement to take in user selected price and pass it to model
	         			  if(priceswitch.equals("$")){
	      			    	double[] prob3 = {1,0,0,0};
	      			    	net.setNodeDefinition("PriceU", prob3);
	           			    net.updateBeliefs();
	      			    }else if(priceswitch.equals("$$")){
	      			    	double[] prob3 = {0,1,0,0};
	      			    	net.setNodeDefinition("PriceU", prob3);
	           			    net.updateBeliefs();
	      			    }else if(priceswitch.equals("$$$")){
	      			    	double[] prob3 = {0,0,1,0};
	      			    	net.setNodeDefinition("PriceU", prob3);
	           			   net.updateBeliefs();
	      			    }else if(priceswitch.equals("$$$$")){
	      			    	double[] prob3 = {0,0,0,1};
	      			    	net.setNodeDefinition("PriceU", prob3);
	           			    net.updateBeliefs();
	      			    }
	         			  
	         			//if statement to take in user selected category, and pass it into the model utility node
	         			   if(ratingswitch.equals("1")){
	        			    	double[] prob4 = {1,0,0,0,0,0,0,0,0};
	        			    	net.setNodeDefinition("RatingU", prob4);
	             			    net.updateBeliefs();
	        			    }else if(ratingswitch.equals("1.5")){
	        			    	double[] prob4 = {0,1,0,0,0,0,0,0,0};
	        			    	net.setNodeDefinition("RatingU", prob4);
	             			    net.updateBeliefs();
	        			    }else if(ratingswitch.equals("2")){
	        			    	double[] prob4 = {0,0,1,0,0,0,0,0,0};
	        			    	net.setNodeDefinition("RatingU", prob4);
	             			    net.updateBeliefs();
	        			    }else if(ratingswitch.equals("2.5")){
	        			    	double[] prob4 = {0,0,0,1,0,0,0,0,0};
	        			    	net.setNodeDefinition("RatingU", prob4);
	             			   net.updateBeliefs();
	        			    }else if(ratingswitch.equals("3")){
	        			    	double[] prob4 = {0,0,0,0,1,0,0,0,0};
	        			    	net.setNodeDefinition("RatingU", prob4);
	             			    net.updateBeliefs();
	        			    }else if(ratingswitch.equals("3.5")){
	        			    	double[] prob4 = {0,0,0,0,0,1,0,0,0};
	        			    	net.setNodeDefinition("RatingU", prob4);
	             			    net.updateBeliefs();
	        			    }else if(ratingswitch.equals("4")){
	        			    	double[] prob4 = {0,0,0,0,0,0,1,0,0};
	        			    	net.setNodeDefinition("RatingU", prob4);
	             			    net.updateBeliefs();
	        			    }else if(ratingswitch.equals("4.5")){
	        			    	double[] prob4 = {0,0,0,0,0,0,0,1,0};
	        			    	net.setNodeDefinition("RatingU", prob4);
	             			    net.updateBeliefs();
	        			    }else if(ratingswitch.equals("5")){
	        			    	double[] prob4 = {0,0,0,0,0,0,0,0,1};
	        			    	net.setNodeDefinition("RatingU", prob4);
	             			    net.updateBeliefs();
	        			    }
	         			   
	         			      //if statement to take in user selected preferences and apply it to the ALU node in model
		         			  if(miswitch.equals("Neighborhood")){
			         				 if(liswitch.equals("Neighborhood")){
			 	      			    	double[] prob5 = {1,1,1,1};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	           			    //inform user that no preferences were set if both comboboxes were the same
			 	           			    rankings.append("Least and most important were the same value. No preferences set!\n\n");
			 	      			    }else if(liswitch.equals("Category")){
			 	      			    	double[] prob5 = {.75,.25,.5,.5};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	      			    }else if(liswitch.equals("Price")){
			 	      			    	double[] prob5 = {.75,.5,.25,.5};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			   net.updateBeliefs();
			 	      			    }else if(liswitch.equals("Rating")){
			 	      			    	double[] prob5 = {.75,.5,.5,.25};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	      			    }
		      			    }else if(miswitch.equals("Category")){
			      			    	if(liswitch.equals("Neighborhood")){
			 	      			    	double[] prob5 = {.25,.75,.5,.5};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	      			    }else if(liswitch.equals("Category")){
			 	      			    	double[] prob5 = {1,1,1,1};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	           			    //inform user that no preferences were set if both comboboxes were the same
			 	           			    rankings.append("Least and most important were the same value. No preferences set!\n\n");
			 	      			    }else if(liswitch.equals("Price")){
			 	      			    	double[] prob5 = {.5,.75,.25,.5};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	      			    }else if(liswitch.equals("Rating")){
			 	      			    	double[] prob5 = {.5,.75,.5,.25};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	      			    }
		      			    }else if(miswitch.equals("Price")){
			      			    	if(liswitch.equals("Neighborhood")){
			 	      			    	double[] prob5 = {.25,.5,.75,.5};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	      			    }else if(liswitch.equals("Category")){
			 	      			    	double[] prob5 = {.5,.25,.75,.5};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	      			    }else if(liswitch.equals("Price")){
			 	      			    	double[] prob5 = {1,1,1,1};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	           			    //inform user that no preferences were set if both comboboxes were the same
			 	           			    rankings.append("Least and most important were the same value. No preferences set!\n\n");
			 	      			    }else if(liswitch.equals("Rating")){
			 	      			    	double[] prob5 = {.5,.5,.75,.25};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	      			    }
		      			    }else if(miswitch.equals("Rating")){
			      			    	if(liswitch.equals("Neighborhood")){
			 	      			    	double[] prob5 = {.25,.5,.5,.75};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	      			    }else if(liswitch.equals("Category")){
			 	      			    	double[] prob5 = {.5,.25,.5,.75};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	      			    }else if(liswitch.equals("Price")){
			 	      			    	double[] prob5 = {.5,.5,.25,.75};
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	      			    }else if(liswitch.equals("Rating")){
			 	      			    	double[] prob5 = {1,1,1,1};
			 	      			        //inform user that no preferences were set if both comboboxes were the same
			 	      			    	rankings.append("Least and most important were the same value. No preferences set!\n\n");
			 	      			    	net.setNodeDefinition("Total", prob5);
			 	           			    net.updateBeliefs();
			 	      			    }
		      			    }
		         			  //try statement to connect to database with the correct hostname, username, and password
		         			 try (Connection connection = DriverManager.getConnection(url, username, password)) {
		         				 
		         			   //test feedback that connection was successful
		                 	   //rankings.append("Database connected!" +"\n");
		         				 
		                 	   //declare statement and result set
		                 	   Statement stmt;
		                 	   ResultSet rs;
		                 	   //set up connection to receive query
		                 	   stmt = connection.createStatement();
		                 	   
		                 	   //build query based on user selections
			         		   String query = "Select restaurant_name, address, phone_number, url, neighborhood, food_category, price_point, rating from restaurants.restaurants";
			         		   //fill result set with all restaurants in database
			         		   rs = stmt.executeQuery(query);
			         		   
			         		   
			         		   
			         		   
			         		   
			         		   //large loop to iterate through every restaurant, set node evidence values, and retrieve ratings for each restaurant
		                 	   while (rs.next()) {
		                 		   
		                 		   //accounting for underscores in neighborhood model
		                 		   if(rs.getString("neighborhood").equals("South Side")){
		                 			  net.setEvidence("Neighborhood", "South_Side");
		                 		   }else if(rs.getString("neighborhood").equals("North Side")){
		                 			  net.setEvidence("Neighborhood", "North_Side"); 
		                 		   }else if(rs.getString("neighborhood").equals("Strip District")){
		                 			  net.setEvidence("Neighborhood", "Strip_District"); 
		                 		   }else if(rs.getString("neighborhood").equals("Squirrel Hill")){
		                 			  net.setEvidence("Neighborhood", "Squirrel_Hill"); 
		                 		   }else if(rs.getString("neighborhood").equals("Downtown")){
		                 			  net.setEvidence("Neighborhood", "Downtown"); 
		                 		   }else if(rs.getString("neighborhood").equals("Oakland")){
		                 			  net.setEvidence("Neighborhood", "Oakland"); 
		                 		   }else if(rs.getString("neighborhood").equals("Shadyside")){
		                 			  net.setEvidence("Neighborhood", "Shadyside"); 
		                 		   }else if(rs.getString("neighborhood").equals("Lawrenceville")){
		                 			  net.setEvidence("Neighborhood", "Lawrenceville"); 
		                 		   }
		                 		   net.updateBeliefs();
		                 		  
			         			   net.setEvidence("Category", rs.getString("food_category"));
			         			   net.updateBeliefs();
			         			   //accounting for difference between $ in interface and D in model
			         			   if(rs.getString("price_point").equals("$")){
			         				  net.setEvidence("Price", "D");
			         			   }else if(rs.getString("price_point").equals("$$")){
			         				  net.setEvidence("Price", "DD"); 
			         			   }else if(rs.getString("price_point").equals("$$$")){
			         				  net.setEvidence("Price", "DDD"); 
			         			   }else if(rs.getString("price_point").equals("$$$$")){
			         				  net.setEvidence("Price", "DDDD"); 
			         			   }
			         			   net.updateBeliefs();
			         			  
			         			   //accounting for difference between decimals in interface and numbers in model
			         			  if(rs.getString("rating").equals("1")){
			         				  net.setEvidence("Rating", "R1");
			         				 
			         			   }else if(rs.getString("rating").equals("1.5")){
			         				  net.setEvidence("Rating", "R15"); 
			         				 
			         			   }else if(rs.getString("rating").equals("2")){
			         				  net.setEvidence("Rating", "R2"); 
			         				 
			         			   }else if(rs.getString("rating").equals("2.5")){
			         				  net.setEvidence("Rating", "R25"); 
			         				
			         			   }else if(rs.getString("rating").equals("3")){
			         				  net.setEvidence("Rating", "R3"); 
			         				
			         			   }else if(rs.getString("rating").equals("3.5")){
			         				  net.setEvidence("Rating", "R35"); 
			         				
			         			   }else if(rs.getString("rating").equals("4")){
			         				  net.setEvidence("Rating", "R4"); 
			         				
			         			   }else if(rs.getString("rating").equals("4.5")){
			         				  net.setEvidence("Rating", "R45"); 
			         				 
			         			   }else if(rs.getString("rating").equals("5")){
			         				  net.setEvidence("Rating", "R5"); 
			         				 
			         			   }
			         			   net.updateBeliefs();
			         			   
			         			   
			         			   //create double array to hold the calculated rank from ALU node
			         			   double[] totalResult =  net.getNodeValue("Total");
			         			   net.updateBeliefs();
			         			   
			         			   //create variable to store the utility variable
			         			   Double rankerdoub = totalResult[0];
			         			   
			         			   //create restaurant object
			         			   restaurantEntry updateList = new restaurantEntry(rs.getString("restaurant_name"),rs.getString("address"),rs.getString("phone_number"),rs.getString("url"),rs.getString("neighborhood"),rs.getString("food_category"),rs.getString("price_point"),rs.getString("rating"),rankerdoub);
			         			   //add the object to the final array
			         			   returnedresults.add(updateList);
		         	           } 
		                 	    //catch any sql errors
		                 	} catch (SQLException z) {
		                 	    rankings.append("Error!");
		                 	    throw new IllegalStateException("Cannot connect the database!", z);
		                 	}
		         			//sort restaurant array based on utility value, this will give best results at top of list
		         			Collections.sort(returnedresults, new RestaurantComparator());
		         			 
		         			//for loop to print desired number of restaurants to the textarea for viewing
		         			for ( int i=0; i<60; i++) {
		         				//get values from object
		         				Object selection = returnedresults.get(i);
		         				String placeName = ((restaurantEntry) selection).getName();
		         				String placeAddress = ((restaurantEntry) selection).getAddress();
		         				String placePhone = ((restaurantEntry) selection).getPhone();
		         				String placeUrl = ((restaurantEntry) selection).getUrl();
		         				String placeNeighborhood = ((restaurantEntry) selection).getNeighborhood();
		         				String placeFood = ((restaurantEntry) selection).getFood();
		         				String placePrice = ((restaurantEntry) selection).getPrice();
		         				String placeRating = ((restaurantEntry) selection).getRating();
		         				Double placeRanking = ((restaurantEntry) selection).getValue();
		         				//convert utility value to string to present
			         			String placeRankingString = placeRanking.toString();
		         				
			         			//print strings to textarea
		         				rankings.append("Name: "+placeName+"\n");
		         				rankings.append("Address: "+placeAddress+"\n");
		         				rankings.append("Phone Number: "+placePhone+"\n");
		         				rankings.append("Website: "+placeUrl+"\n");
		         				rankings.append("Neighborhood: "+placeNeighborhood+"\n");
		         				rankings.append("Food Type: "+placeFood+"\n");
		         				rankings.append("Price Point: "+placePrice+"\n");
		         				rankings.append("Rating: "+placeRating+"\n");
		         				rankings.append("Calculated Ranking: "+placeRankingString+"\n\n");
		         				}
		         			//position cursor at top of text area to show best results first
		         			   rankings.setCaretPosition(0);
                	 }//catch any smile exceptions
        			 catch (SMILEException x) {
        				 rankings.setText(x.getMessage());
        			 }
                }
            });
	
	        //Place elements in GUI
            c.gridx = 0;
            c.gridy = 1;
	        mainPanel.add(title,c);
	        c.gridx = 0;
            c.gridy = 2;
	        mainPanel.add(place,c);
	        c.gridx = 0;
            c.gridy = 3;
	        mainPanel.add(comboPlace,c);
	        c.gridx = 0;
            c.gridy = 4;
	        mainPanel.add(type,c);
	        c.gridx = 0;
            c.gridy = 5;
	        mainPanel.add(comboFood,c);
	        c.gridx = 0;
            c.gridy = 6;
	        mainPanel.add(ppoint,c);
	        c.gridx = 0;
            c.gridy = 7;
	        mainPanel.add(comboPrice,c);
	        c.gridx = 0;
            c.gridy = 8;
	        mainPanel.add(rate,c);
	        c.gridx = 0;
            c.gridy = 9;
	        mainPanel.add(comboRate,c);
	        c.gridx = 0;
            c.gridy = 10;
	        mainPanel.add(pref,c);
	        c.gridx = 0;
            c.gridy = 11;
	        mainPanel.add(mi,c);
	        c.gridx = 1;
            c.gridy = 11;
	        mainPanel.add(li,c);
	        c.gridx = 0;
            c.gridy = 12;
	        mainPanel.add(micombo,c);
	        c.gridx = 1;
            c.gridy = 12;
	        mainPanel.add(licombo,c);
	        c.gridx = 0;
            c.gridy = 13;
	        mainPanel.add(submit,c);
	        
	        //Place TextArea for results in separate panel
	        d.gridx = 0;
            d.gridy = 1;
            restaurantPanel.add(lister,d);
	        d.gridx = 0;
            d.gridy = 2;
            restaurantPanel.add(scrollPane,d);

	        //add main panel with components to frame on top
	        mainFrame.add(mainPanel,BorderLayout.WEST);
	        //add banner panel with banner to frame on bottom
	        mainFrame.add(restaurantPanel, BorderLayout.EAST);
	        //pack the frame to display elements
	        mainFrame.pack();
	
  }
}