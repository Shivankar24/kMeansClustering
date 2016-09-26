import java.awt.BorderLayout;
import java.awt.Panel;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class HW05_Ojha_Shivankar_kMeans {

	
	/** 
	 * Class containing functional implementations of finding
	 * the best value of k for kmeans clustering and the associated values of the 
	 * SSE for each cluster. 
	
	 *                                    
	 * @author      Shivankar Ojha
	 * 
	 */	
	
	public static ArrayList<ArrayList<Float>> clean_data(ArrayList<ArrayList<Float>> data)
	{
		/**
		 * This method cleans the data which does not seem relevant to the clustering 
		 * process. I have used the criteria that if a data point lies near the axes and far 
		 * away from high density areas, I would discard them. 
		 * It returns the cleaned data in the form of an array list of array lists. 
		 * 
		 * @param	data			Array list containing arraylists, each of which stores one particular attribute values.
		
		 
		 */
		
		ArrayList<ArrayList<Float>> cleaned_data=new ArrayList<ArrayList<Float>>();
		float x1,y1,z1,x2,y2,z2,distance;int close_points;
		ArrayList<Float> cleaned_x=new ArrayList<Float>();
		ArrayList<Float> cleaned_y=new ArrayList<Float>();
		ArrayList<Float> cleaned_z=new ArrayList<Float>();
		for (int i=0;i<data.get(0).size();i++)
		{
			close_points=0;
			x1=data.get(0).get(i);y1=data.get(1).get(i);z1=data.get(2).get(i);
			for (int j=0;j<data.get(0).size();j++)
			{
				x2=data.get(0).get(j);y2=data.get(1).get(j);z2=data.get(2).get(j);
				distance=get_distance(x1,y1,z1,x2,y2,z2);
				if (distance<1)
					close_points++;
			}
			//ignoring least significant points
			if (x1<1 || y1<1 || z1<1)
				continue;
			if (close_points>10) //195
			{
				cleaned_x.add(x1);
				cleaned_y.add(y1);
				cleaned_z.add(z1);
			}
		}
		cleaned_data.add(cleaned_x);
		cleaned_data.add(cleaned_y);
		cleaned_data.add(cleaned_z);
		return cleaned_data;
	}
	public static float get_distance(float x1, float y1, float z1, float x2,
			float y2, float z2) {
		
		// Function to find the distance between two points. 
		
		float distance=(float) Math.pow((x1-x2),2)+(float) Math.pow((y1-y2),2)+(float) Math.pow((z1-z2),2);
		return distance;
	}
	public static ArrayList<Cluster> k_means(ArrayList<ArrayList<Float>> data, int k) {
		/**
		 * This method finds the k clusters for the given data, and returns it in the form of 
		 * an array list of the objects of the cluster class, each value of the array list making up
		 * one of the k clusters.  
		 * 
		 * @param	data			Array list containing arraylists, each of which stores one particular attribute values.
		 * @param 	k				Thsi value specifies the number of clusters required for clustering. 
		
		 
		 */
		ArrayList<Float> clustered_data=new ArrayList<Float>();
		ArrayList<Cluster> clusters=new ArrayList<Cluster>();
		Cluster c = new Cluster();
		ArrayList<Float> x_values,y_values,z_values,centroid;
		
	
		int size_of_clusters=(int) (data.get(0).size()/(float)k);
		
		//The following code is to select the initial prototypes as the centroids of the partitions of data. 
		
		for (int i=0;i<k;i++)
		{
			x_values=new ArrayList<Float>();
			y_values=new ArrayList<Float>();
			z_values=new ArrayList<Float>();
			c=new Cluster();
			c.points=new ArrayList<ArrayList<Float>>();
			c.centroid=new ArrayList<Float>();
			for (int j=i*size_of_clusters;j<(i+1)*size_of_clusters;j++)
			{
			x_values.add(data.get(0).get(j));	
			y_values.add(data.get(1).get(j));
			z_values.add(data.get(2).get(j));
			}

			c.points.add(x_values);c.points.add(y_values);c.points.add(z_values);
			c.centroid.add(find_centroid(x_values));c.centroid.add(find_centroid(y_values));
			c.centroid.add(find_centroid(z_values));
			clusters.add(c);
		}
		
		float x = 0,y=0,z=0,centroid_x,centroid_y,centroid_z,dist,min_dist;int index=0,min_dist_index=0;
		float distance_between_centroid=0f;
		ArrayList<Cluster> new_clusters=new ArrayList<Cluster>();
		new_clusters=clusters;

		//Finding the new centroid after assigning each point to closest cluster and then updating the centroid
		//until the centroids stop moving significantly. 
		do{
		
		for (int k1=0;k1<new_clusters.size();k1++)
		{
			ArrayList<Float> new_x=new ArrayList<Float>();
			ArrayList<Float> new_y=new ArrayList<Float>();
			ArrayList<Float> new_z=new ArrayList<Float>();
			ArrayList<Float> initial_centroid=new ArrayList<Float>();
			initial_centroid=new_clusters.get(k1).centroid;
		for (int i=0;i<data.get(0).size();i++)
		{
			c=new Cluster();
			c.points=new ArrayList<ArrayList<Float>>();
			c.centroid=new ArrayList<Float>();
			x=data.get(0).get(i);y=data.get(1).get(i);z=data.get(2).get(i);
			
			min_dist=999f;
			for (int j=0;j<new_clusters.size();j++)
			{
				Cluster c1=new Cluster();
				c1=new_clusters.get(j);
				centroid_x=c1.centroid.get(0);centroid_y=c1.centroid.get(1);centroid_z=c1.centroid.get(2);
				dist=get_distance(x,y,z,centroid_x,centroid_y,centroid_z);
				if (dist<min_dist)
				{
					
					min_dist=dist;
					min_dist_index=j;
				}
						
			}
			if (min_dist_index==k1)
			{
				new_x.add(x);new_y.add(y);new_z.add(z);
			}
			
		}
		
		
		c.points.add(new_x);c.points.add(new_y);c.points.add(new_z);
		c.centroid.add(find_centroid(c.points.get(0)));c.centroid.add(find_centroid(c.points.get(1)));
		c.centroid.add(find_centroid(c.points.get(2)));
		
		distance_between_centroid=get_distance(c.centroid.get(0),c.centroid.get(1),c.centroid.get(2),
				initial_centroid.get(0),initial_centroid.get(1),initial_centroid.get(2));
		
		new_clusters.set(k1,c);
		
		}
		}while (distance_between_centroid>0.0001);
		//checking the distance between initial and old centroid.
		
	return(new_clusters);
			
	}
	public static void find_black_hole(ArrayList<ArrayList<Float>> data)
	{
		//Function to find the black hole based on data points in the vicinity
		int no_of_nearby_points=0;int min_no_of_nearby_points=999999;
		float x1,y1,z1,x2,y2,z2,x = 0,y = 0,z = 0;
		float dist;
		for (int i=0;i<data.get(0).size();i++)
		{
			no_of_nearby_points=0;
			x1=data.get(0).get(i);y1=data.get(1).get(i);z1=data.get(2).get(i);
			for (int j=0;j<data.get(0).size();j++)
			{
				x2=data.get(0).get(j);y2=data.get(1).get(j);z2=data.get(2).get(j);
				dist=get_distance(x1,y1,z1,x2,y2,z2);
				if (dist<100)
					no_of_nearby_points++;
			}
			if (no_of_nearby_points<min_no_of_nearby_points ||no_of_nearby_points==0)
			{
				min_no_of_nearby_points=no_of_nearby_points;
				x=x1;y=y1;z=z1;
			}
		}
		System.out.println("Likely position of black hole:"+x+" "+y+" "+" "+z);
	}
	public static float find_centroid(ArrayList<Float> data)
	{	
		//Function to find the centroid. 
		float sum = 0;
		for (int i=0;i<data.size();i++)
			sum=sum+data.get(i);
		float coordinate=sum/data.size();
		return coordinate;
	}
	public static float compute_SSE(ArrayList<Cluster> clustering)
	{
		//Function to compute the SSE of the given list of clusters. 
		Cluster cluster=new Cluster();float SSE=0f;
		float x,y,z,centroid_x,centroid_y = 0,centroid_z = 0;
		for (int i=0;i<clustering.size();i++)
		{
			cluster=new Cluster();
			cluster=clustering.get(i);
			centroid_x=cluster.centroid.get(0);centroid_y=cluster.centroid.get(1);centroid_z=cluster.centroid.get(2);
			for (int j=0;j<cluster.points.get(0).size();j++)
			{
				x=cluster.points.get(0).get(j);y=cluster.points.get(1).get(j);z=cluster.points.get(2).get(j);
				SSE=(float) (SSE+Math.pow(get_distance(x,y,z,centroid_x,centroid_y,centroid_z),2));
			}
		}
		return SSE;
	}
	public static Panel plot_SSE(ArrayList<Float> data,String title,String x_axis,String y_axis)
	{
		/**
		 * This method is used to generate curves and graphs of the data provided to it. 
		 * 
		 *
		 * @param       data    Array list containing the data values for the graph to be plotted. 
		
		 
		 */
		DefaultCategoryDataset barchartdata=new DefaultCategoryDataset();
		for (int i=0;i<=data.size()-2;i+=2)
		{
			float x=data.get(i);
			float y=data.get(i+1);
			barchartdata.setValue(y,"",""+(x));
			
		}
		
		

		JFreeChart barchart=ChartFactory.createLineChart(title, x_axis, y_axis, barchartdata);
     	ChartPanel barpanel=new ChartPanel(barchart);
		Panel p=new Panel();
		p.setSize(1000,1000);
		p.add(barpanel,"Center");
		return p;
		
	}

	public static Panel scatter_chart(ArrayList<Cluster> c, String title,String x_axis,String y_axis)
	{
		/**
		 * This method is used to generate the scatter chart of the data provided to it. 
		 * 
		 *
		 * @param       cluster   Array list containing clusters which are to be plotted.  
		
		 
		 */
		XYSeriesCollection scatterplotdata = new XYSeriesCollection();
	    XYSeries dataset = new XYSeries("test 1");
	    for (int i = 0; i <c.size(); i++) {
	    	for (int j=0;j<c.get(i).points.get(0).size();j++)
	    	{
	        float x = c.get(i).points.get(0).get(j);
	        float y = c.get(i).points.get(1).get(j);
	        dataset.add(x, y);
	    	}
	    }
	    scatterplotdata.addSeries(dataset);
	    JFreeChart barchart=ChartFactory.createScatterPlot(title, x_axis, y_axis,  scatterplotdata);
		
		
     	ChartPanel barpanel=new ChartPanel(barchart);
		Panel p=new Panel();
		p.setSize(1000,1000);
		p.add(barpanel,"Center");
		return p;
		
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	System.out.println("For the data set: HW_KMEANS_DATA_v015.csv");
		
		try {
			
			
			BufferedReader inp = new BufferedReader(new FileReader("HW_KMEANS_DATA_v015.csv"));
			String s=inp.readLine();
			ArrayList<Float> attr1=new ArrayList<Float>();
			ArrayList<Float> attr2=new ArrayList<Float>();
			ArrayList<Float> attr3=new ArrayList<Float>();
			//ArrayList<Float> class_value=new ArrayList<Float>();
			int counter=0;
			
			/* Reading data from the data set and string it in an array list. 
			 * Each attributes values is read into a separate array list and there is a 
			 * separate array list to store the class values. 
			 * The three attributes array list are in turn added to an array list of float 
			 * type array lists, which is what is send to the function. 
			 */	
			
			while (s!=null)
			{			   
			      
			      String[] s1=s.split(",");
			      for (int i=0;i<s1.length;i++)
			      {
			    	  float x=Float.parseFloat(s1[i]);
			    	  switch (i)
			    	  {
			    		  case 0:attr1.add(x);break;
			    		  case 1:attr2.add(x);break;
			    		  case 2:attr3.add(x);break;
			    	  }
			      }
			      s=inp.readLine();		
			}
			ArrayList<ArrayList<Float>> data=new ArrayList<ArrayList<Float>>();
			ArrayList<ArrayList<Float>> cleaned_data=new ArrayList<ArrayList<Float>>();
			data.add(attr1);
			data.add(attr2);
			data.add(attr3);
			ArrayList<String> attribute_names=new ArrayList<String>();
			attribute_names.add("attr1");
			attribute_names.add("attr2");
			attribute_names.add("attr3");
			cleaned_data=clean_data(data);
			ArrayList<ArrayList<Cluster>> clustering=new ArrayList<ArrayList<Cluster>>();
			float x,y,z;
			for (int k=1;k<11;k++)
			{
				clustering.add(k_means(cleaned_data,k));
		}
			ArrayList<Float> plot_data=new ArrayList<Float>();
			float SSE;
			//SSE vs k PLOT
			for (int i=0;i<clustering.size();i++)
			{
				System.out.print("For cluster k="+ (i+1)+", " );
				SSE=compute_SSE(clustering.get(i));
				
				System.out.println("SSE="+SSE);
				plot_data.add((float)(i+1));
				plot_data.add(SSE);
			}
			
			Panel p=new Panel();
			p=plot_SSE(plot_data,"SSE vs k","k","SSE");
			JFrame f=new JFrame();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        f.setLayout(new BorderLayout(0, 5));
	        f.add(p, BorderLayout.CENTER);
			f.setSize(1000, 500);
			f.add(p);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setVisible(true);
			
			ArrayList<Cluster> c5=new ArrayList<Cluster>();
			c5=clustering.get(4);
			Cluster c1=new Cluster();
			Cluster c2=new Cluster();
			float a,b,c;
			
			//sorting the clusters on the basis of points in them
			System.out.println("Sorted number of points in each cluster for k=5 are: ");
			for (int i=0;i<c5.size();i++)
			{
				c1=c5.get(i);
				a=c1.points.get(0).size();
				for (int j=i+1;j<c5.size();j++)
				{
					c2=c5.get(j);
					b=c2.points.get(0).size();
					if (b<a)
					{
						c5.set(i, c2);
						c5.set(j,c1);
					}
					
				}
			}
			//Clusters for k=5 graph. 
			
			for (int i=0;i<c5.size();i++)
			{
				
				System.out.print("Points in cluster "+(i+1)+":");
				System.out.println(c5.get(i).points.get(0).size());
			}
			
			Panel p1=new Panel();
			p1=scatter_chart(c5,"Clusters for k=5","X values","Y values");
			f=new JFrame();
			f.setSize(1000, 500);
			f.add("Center",p1);
			f.setVisible(true);
			find_black_hole(cleaned_data);
			
		}
			
		catch(FileNotFoundException f)
			{
		System.out.println("File HW_KMEANS_DATA_v015.csv not found!! ");
			}

	}
	
	

}
class Cluster
{
	
	/** 
	 * Class representing a cluster for the above representation
	 * The two  class variables are as follows: 
	 * 
	 * ArrayList<Float> 				centroid      This  is used to store the centroid of the cluster object in consideration. 
	 * ArrayList<ArrayList<Float>> 		points  	  An array list containing three array lists, having x,y and z values respectively. 
	 *                                    
	 * @author      Shivankar Ojha
	 * 
	 
	 */
	public ArrayList<Float> centroid;
	ArrayList<ArrayList<Float>> points;
}