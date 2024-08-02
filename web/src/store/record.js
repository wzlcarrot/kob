import { defineStore } from 'pinia';
import  { ref } from 'vue';


export const useRecordStore = defineStore('record',()=>{
    const is_record = ref(false);
    const a_steps = ref("");
    const b_steps = ref("");
    const record_loser = ref("");

    const updateRecord = (value) => {
        is_record.value = value;
    }

    const updateStep = (step) => {
        a_steps.value = step;
        b_steps.value = step;
    }

    const updateLoser = (loser) => {
        record_loser.value = loser;
    
    }
    return {is_record, a_steps, b_steps,record_loser, updateRecord, updateStep,updateLoser};

});