package com.example.wjk232.runningsum;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nz.sodium.Cell;
import nz.sodium.CellLoop;
import nz.sodium.CellSink;
import nz.sodium.Handler;
import nz.sodium.Lambda1;
import nz.sodium.Lambda2;
import nz.sodium.Lambda3;
import nz.sodium.Stream;
import nz.sodium.StreamSink;
import nz.sodium.Transaction;
import nz.sodium.Unit;

public class MainActivity extends AppCompatActivity {

    Random myRandom = new Random();

    StreamSink<Integer> nextRandom     = new StreamSink<>();

    StreamSink<Unit>    incrementEvent = new StreamSink<>();
    StreamSink<Unit>    decrementEvent = new StreamSink<>();
    StreamSink<Integer> summing = new StreamSink<>();

    CellSink<Integer> max = new CellSink<>(9);
    CellSink<Integer> min = new CellSink<>(3);
    CellLoop<Integer>            N;
    CellLoop<ArrayList<Integer>> lastNValues;
    Cell<Integer>                sum ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView sumView = (TextView)findViewById(R.id.Sum);
        final TextView nView = (TextView)findViewById(R.id.N);

        //transaction for closing loops
        Transaction.runVoid(new Runnable() {
            @Override
            public void run() {
                // reactive network
                N = new CellLoop<>();
                lastNValues = new CellLoop<>();


                Stream < Integer > incrementValues = incrementEvent.snapshot(N, new Lambda2<Unit, Integer, Integer>() {
                    @Override
                    public Integer apply(Unit unit, Integer old_value) {
                        return old_value + 1;
                    }
                });
                Stream <ArrayList<Integer>> randValues = nextRandom.snapshot(lastNValues, new Lambda2<Integer, ArrayList<Integer>, ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> apply(Integer rand, ArrayList<Integer> list) {
                        ArrayList<Integer> temp = new ArrayList<Integer>();
                        if(list == null) temp.add(rand);
                        else{
                            temp = list;
                            temp.add(rand);
                        }
                        return temp;
                    }
                });

                Stream<Integer> decrementValues = decrementEvent.snapshot(N, new Lambda2<Unit, Integer, Integer>() {
                    @Override
                    public Integer apply(Unit unit, Integer old_value) {
                        return old_value-1;
                    }
                });

                Cell<Integer> candidateValues = incrementValues.orElse(decrementValues).hold(10);

                Cell<Integer> legalValues =
                        candidateValues.lift(min, max, new Lambda3<Integer, Integer, Integer, Integer>() {
                            @Override
                            public Integer apply(Integer change, Integer min, Integer max) {
                                if(change > max) return max;
                                if(change < min) return min;
                                return change;
                            }
                        });

                Cell<ArrayList<Integer>> rand = randValues.hold(null);
                Cell<ArrayList<Integer>> lastN = rand.lift(N, new Lambda2< ArrayList<Integer>,Integer, ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> apply(ArrayList<Integer> list, Integer maxLength) {
                        if(list != null && list.size() > maxLength ){
                            list.remove(0);
                        }
                        return list;
                    }
                });
                sum = lastNValues.map(new Lambda1<ArrayList<Integer>, Integer>() {
                    @Override
                    public Integer apply(ArrayList<Integer> integers) {
                        Integer sum=0;
                        if(integers != null) {
                            for (int i = 0; i < integers.size(); i++) {
                                sum += integers.get(i);
                            }
                        }
                        return sum;
                    }
                });

                N.loop(legalValues);
                lastNValues.loop(lastN);
            }
        });
        N.listen(new Handler<Integer>() {
            @Override
            public void run(Integer value) {
                nView.setText(value.toString());
            }
        });
        lastNValues.listen(new Handler<ArrayList<Integer>>() {
            @Override
            public void run(ArrayList<Integer> integers) {
                if(integers !=null)
                    Log.d("list",integers.toString());
            }
        });

        sum.listen(new Handler<Integer>() {
            @Override
            public void run(Integer integer) {
                if(lastNValues != null)
                    sumView.setText(integer.toString());
            }
        });
    }

    public void sendNumber(View view){
        nextRandom.send(myRandom.nextInt(9)+1);
    }

    public void decN(View view){
        decrementEvent.send(Unit.UNIT);
    }

    public void incN(View view){
        incrementEvent.send(Unit.UNIT);
    }

}
