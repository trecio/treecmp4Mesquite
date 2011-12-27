/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common.clustering;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Damian
 */
public class ClusterQTFinder {

    private int Num;
    private boolean[] clustersPonits;
    private DistCalculator calc;
    
    
    public ClusterQTFinder(int numPoints, DistCalculator calc_) {
    
        this.Num=numPoints;
        this.clustersPonits=new boolean[numPoints];
        this.calc=calc_;
    }

    ArrayList<int[]> calculateClusters(double maxDiameter)
    {      
        return calculateClusters(maxDiameter,1);
    }
    
    ArrayList<int[]> calculateClusters(double maxDiameter,int clustMinSize)
    {        
        ArrayList<int[]> finalClusters=new ArrayList<int[]>();
        ArrayList<int[]> tempClusters=new ArrayList<int[]>();
        
        clearClusterdPoints();
        
        do
        { 
            tempClusters.clear();
             
            for (int i=0;i<Num;i++)
            {
                if (isClustered(i)) continue;
            
                //int [] clustCandidate=createClusterStartFrom(i,maxDiameter);
                int [] clustCandidate=fastCreateClusterStartFrom(i,maxDiameter);
                if(clustCandidate.length>=clustMinSize)
                {
                    tempClusters.add(clustCandidate);
                }
                                
            }
            
            if(!tempClusters.isEmpty())
            {
                int[] bestActualCluster=findBestCluster(tempClusters);
                finalClusters.add(bestActualCluster);
                
                for(int j=0;j<bestActualCluster.length;j++)
                    setClustered(bestActualCluster[j]);                
            }                
        }while(!tempClusters.isEmpty());
            
        return finalClusters;
    }
    
    public int[] findBestCluster(ArrayList<int[]> clusters)
    {
        int bestCluster=-1;
        int bestClusterSize=-1;
        int size;
        for(int i=0;i<clusters.size();i++)
        {
         
         //mozemy dodac warunek ktory wybrac jesli jest wiele o max size
         //moze ten ktorego srednica jest najmniejsza   
         size=clusters.get(i).length;
         if(size>bestClusterSize)
         {
             bestCluster=i;
             bestClusterSize=size;
         }
            
       }       
                
        return clusters.get(bestCluster);
    }
    
    
    private void setClustered(int point)
    {
        this.clustersPonits[point]=true;
    }
    
    private boolean isClustered(int point)
    {
        return this.clustersPonits[point];
        
    }
    private void clearClusterdPoints()       
    {
        for (int i=0;i<this.Num;i++)
            this.clustersPonits[i]=false;        
    }
    
    
    double distToCluster(ArrayList<Integer> cluster,int point)
    {
        double dist=-1;
        double temp;
        int size=cluster.size();
        for(int i=0; i<size;i++)
        {
            temp=calc.getDistance(cluster.get(i), point);
            if(temp>dist)
            {
                dist=temp;
            }
        
        }
        return dist;
    }

    int [] createClusterStartFrom(int start,double maxDiameter)
    {
        HashSet<Integer> clusteredSet =new HashSet<Integer>();
        ArrayList<Integer> cluster=new ArrayList<Integer>(); 
        
        boolean continueLoop=true;
        double minDist,temp;
        int minDistPoint;
        
        clusteredSet.clear();
        cluster.clear();
        
        cluster.add(new Integer(start));
        clusteredSet.add(new Integer(start));
              
        while(continueLoop)
        {
            
            minDist=Double.MAX_VALUE;
            minDistPoint=-1;
            //szukamy najlizszego punktu
            for(int i=0;i<this.Num;i++)
            {
                if(i==start || isClustered(i) || clusteredSet.contains(new Integer(i)) ) 
                    continue; 
        
                temp=distToCluster(cluster,i);
                if(temp<minDist)
                {
                    minDist=temp;
                    minDistPoint=i;
                }
            }
        
            if (minDist<=maxDiameter && minDistPoint!=-1)
            {
                cluster.add(new Integer(minDistPoint)); 
                clusteredSet.add(new Integer(minDistPoint));
            }else
            {
                continueLoop=false;
                //end loop
            }
        }
        //przepisujemy zawartosc clusters do tablicy
        int [] clustTab=new int[cluster.size()];
        for(int i=0;i<cluster.size();i++)
            clustTab[i]=cluster.get(i);
        
         return clustTab;
    }
    
    int [] fastCreateClusterStartFrom(int start,double maxDiameter)
    {
        HashSet<Integer> clusteredSet =new HashSet<Integer>();
        ArrayList<Integer> cluster=new ArrayList<Integer>(); 
        //mozemy sprobowac usunoc autoboxing....
        boolean continueLoop=true;
        double minDist;
        int minDistPoint,size;
        double dist,tempDist;
        
        clusteredSet.clear();
        cluster.clear();
        
        cluster.add(new Integer(start));
        clusteredSet.add(new Integer(start));
              
        while(continueLoop)
        {
            
            minDist=Double.MAX_VALUE;
            minDistPoint=-1;
            //szukamy najlizszego punktu
            for(int i=0;i<this.Num;i++)
            {
                if(i==start || isClustered(i) || clusteredSet.contains(new Integer(i)) ) 
                    continue; 
        
                //funkcjonalosc metody disttoCluster
                //optymalizacja
                
                dist=-1;
                size=cluster.size();
                for(int j=0; j<size;j++)
                {
                    tempDist=calc.getDistance(cluster.get(j), i);
                    if(tempDist>dist)
                        dist=tempDist;
                            
                }
                  
                 //temp=distToCluster(cluster,i);
                                
                if(dist<minDist)
                {
                    minDist=dist;
                    minDistPoint=i;
                }
            }
        
            if (minDist<=maxDiameter && minDistPoint!=-1)
            {
                cluster.add(new Integer(minDistPoint)); 
                clusteredSet.add(new Integer(minDistPoint));
            }else
            {
                continueLoop=false;
                //end loop
            }
        }
        //przepisujemy zawartosc clusters do tablicy
        int [] clustTab=new int[cluster.size()];
        for(int i=0;i<cluster.size();i++)
            clustTab[i]=cluster.get(i);
        
         return clustTab;
    }

    /**
     * Only for testing.
     */
    
    public void test()
    {
         final String text="Four score and seven years ago our fathers brought forth on this"
               + " continent a new natisadas sadasd asdasd sadasd asdasd sadasd sadasd dddd dddddddd adadsww www qwwewe wqwewq qwqweqweqwwq wqqwqweqw qweqwwq sadas fdsafasdas wqwqeqwe asdasdasd qweqwedasdasd sadasdsad qweqwe sadasdasd qweqwdsad on conceived in liberty and dedicated to the proposition"
               + " that all men are created equal";
        
        String text2="mama tata bratanek brat";
    // break the text up into an array of individual words
    final String[] words = text.split(" ");
        
        
        testowa test=new testowa();
        ClusterQTFinder cl2= new ClusterQTFinder(words.length,test);
        List<int[]> cos=cl2.calculateClusters(30.0);
        
    }
    
    
    
    
    
    
}
/**
 * Only for testing.
 * 
 * @author Damian
 */
class testowa implements DistCalculator
{

    private String text="Four score and seven years ago our fathers brought forth on this"
               + " continent a new natisadas sadasd asdasd sadasd asdasd sadasd sadasd dddd dddddddd adadsww www qwwewe wqwewq qwqweqweqwwq wqqwqweqw qweqwwq sadas fdsafasdas wqwqeqwe asdasdasd qweqwedasdasd sadasdsad qweqwe sadasdasd qweqwdsad on conceived in liberty and dedicated to the proposition"
               + " that all men are created equal";
    
    public testowa() {
    }

    public double getDistance(int i, int j) {
       // final String[] words = text.split(" ");
         //return Math.abs(words[i].length() - words[j].length());
        return 1;
    }
    
    
}
 